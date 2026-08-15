package com.arclights.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.IntConsumer;

import com.arclights.animation.EntityAnimationController;
import com.arclights.animation.SpriteSizing;
import com.arclights.entity.enemy.Enemy;
import com.arclights.entity.enemy.EnemyType;
import com.arclights.models.GameMap;
import com.arclights.models.GridPoint;
import com.arclights.models.PlayerProgress;
import com.arclights.models.Tile;
import com.arclights.models.wave.EndlessWaveGenerator;
import com.arclights.models.wave.SpawnEntry;
import com.arclights.models.wave.Wave;
import com.arclights.models.wave.WaveConfig;
import com.arclights.ui.EntityLayer;
import com.arclights.ui.MapRenderer;

import javafx.geometry.Point2D;


public class EnemyManager {
    private final List<Enemy> activeEnemies = new ArrayList<>();
    private final Map<Enemy, EntityAnimationController> animations = new HashMap<>();
    private final List<Point2D> enemyPath = new ArrayList<>();
    private final EntityLayer entityLayer;
    private final GameMap gameMap;
    
    // Grid alignment parameters
    private final double tileWidth;
    private final double tileHeight;
    private final double paddingX;
    private final double paddingY;
    private final double offsetX;
    private final double offsetY;

    private int spawnRow = 1;
    private int spawnCol = 0;
    private int targetRow = -1;
    private int targetCol = -1;

    // Cache of BFS-computed default routes, keyed by spawn point, so maps
    // with multiple ENEMY_SPAWN tiles ('S') can each get their own default
    // route to the objective without recomputing every spawn.
    private final Map<GridPoint, List<Point2D>> defaultPathCache = new HashMap<>();

    // Wave/config-driven spawning state
    private List<WaveConfig.ScheduledSpawn> scheduledSpawns = new ArrayList<>();
    private int nextScheduledIndex = 0;
    private double elapsedSeconds = 0;
    private static final double SECONDS_PER_UPDATE = 1.0 / 60.0; // matches App's ~60Hz simulation tick

    // Endless-mode state: when set, waves are generated lazily instead of
    // coming from a fixed, finite WaveConfig. See EndlessWaveGenerator.
    private EndlessWaveGenerator infiniteWaveGenerator = null;
    private int nextGeneratedWaveIndex = 0;
    private double infiniteWaveClock = 0;
    // Always keep at least this many seconds of already-generated spawns
    // queued up ahead of the current clock, so we never generate on the
    // exact frame a wave is due.
    private static final double INFINITE_LOOKAHEAD_SECONDS = 5.0;
    // Once this many spawns have been dispatched, drop them from the front
    // of scheduledSpawns so a long-running endless session doesn't grow the
    // list forever.
    private static final int INFINITE_TRIM_THRESHOLD = 500;

    // Base lives (Arknights-style): every map defaults to 3. An enemy that
    // walks its full path and reaches the objective consumes one life
    // instead of being "killed" — it's removed immediately, no death anim.
    public static final int DEFAULT_LIVES = 3;
    private int lives = DEFAULT_LIVES;
    private boolean gameOver = false;
    private Runnable onGameOverCallback;
    private IntConsumer onLivesChangedCallback;

    // Killing any enemy (as opposed to it leaking through to the objective)
    // rewards crystals.
    public static final int CRYSTALS_PER_KILL = 10;

    // Stage-clear detection: once every scheduled wave has finished spawning
    // and no enemies remain alive on the field, the stage counts as cleared.
    private boolean waveConfigLoaded = false;
    private boolean stageCleared = false;
    private Runnable onStageClearCallback;

    // Constructor updated to accept render dimensions
    public EnemyManager(EntityLayer entityLayer, GameMap gameMap, MapRenderer.RenderResult renderResult) {
        this.entityLayer = entityLayer;
        this.gameMap = gameMap;
        this.tileWidth = renderResult.tileWidth;
        this.tileHeight = renderResult.tileHeight;
        this.paddingX = renderResult.paddingX;
        this.paddingY = renderResult.paddingY;
        this.offsetX = renderResult.offsetX;
        this.offsetY = renderResult.offsetY;
        
        initPath();
    }

    // Overloaded constructor if passing parameters individually
    public EnemyManager(EntityLayer entityLayer, GameMap gameMap, double tileWidth, double tileHeight, double paddingX, double paddingY, double offsetX, double offsetY) {
        this.entityLayer = entityLayer;
        this.gameMap = gameMap;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.paddingX = paddingX;
        this.paddingY = paddingY;
        this.offsetX = offsetX;
        this.offsetY = offsetY;

        initPath();
    }

    private void initPath() {
        int detectedSpawnRow = -1;
        int detectedSpawnCol = -1;
        int detectedTargetRow = -1;
        int detectedTargetCol = -1;

        for (int r = 0; r < gameMap.getRows(); r++) {
            for (int c = 0; c < gameMap.getCols(); c++) {
                Tile tile = gameMap.getTile(r, c);
                if (tile.getTileType() == Tile.TileType.ENEMY_SPAWN) {
                    detectedSpawnRow = r;
                    detectedSpawnCol = c;
                } else if (tile.getTileType() == Tile.TileType.PLAYER_OBJECTIVE) {
                    detectedTargetRow = r;
                    detectedTargetCol = c;
                }
            }
        }

        if (detectedSpawnRow != -1 && detectedSpawnCol != -1) {
            this.spawnRow = detectedSpawnRow;
            this.spawnCol = detectedSpawnCol;
        }
        this.targetRow = detectedTargetRow;
        this.targetCol = detectedTargetCol;

        if (spawnRow == -1 || spawnCol == -1 || targetRow == -1 || targetCol == -1) {
            this.spawnRow = 1;
            this.spawnCol = 0;
            this.targetRow = 3;
            this.targetCol = 7;
        }

        // Default legacy path, kept for backward-compatible spawnEnemy(EnemyType) calls.
        List<Point2D> path = computeBfsPath(new GridPoint(spawnRow, spawnCol));
        enemyPath.addAll(path);
        defaultPathCache.put(new GridPoint(spawnRow, spawnCol), enemyPath);
    }

    /**
     * BFS shortest path (in pixel waypoints) from the given grid spawn point
     * to the map's PLAYER_OBJECTIVE tile. Results are cached per spawn point
     * since a config may reuse the same custom spawn point across many
     * SpawnEntry instances.
     */
    private List<Point2D> computeBfsPath(GridPoint start) {
        List<Point2D> cached = defaultPathCache.get(start);
        if (cached != null) return cached;

        Queue<GridPoint> queue = new LinkedList<>();
        Set<GridPoint> visited = new HashSet<>();
        Map<GridPoint, GridPoint> parentMap = new HashMap<>();
        GridPoint target = new GridPoint(targetRow, targetCol);

        queue.add(start);
        visited.add(start);

        boolean found = false;
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        while (!queue.isEmpty()) {
            GridPoint curr = queue.poll();
            if (curr.r == target.r && curr.c == target.c) {
                found = true;
                break;
            }

            for (int i = 0; i < 4; i++) {
                int nr = curr.r + dr[i];
                int nc = curr.c + dc[i];

                if (nr >= 0 && nr < gameMap.getRows() && nc >= 0 && nc < gameMap.getCols()) {
                    Tile tile = gameMap.getTile(nr, nc);
                    if (tile.isWalkableForEnemies()) {
                        GridPoint neighbor = new GridPoint(nr, nc);
                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);
                            parentMap.put(neighbor, curr);
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }

        List<Point2D> path = new ArrayList<>();
        if (found) {
            GridPoint curr = target;
            while (curr != null) {
                path.add(0, new Point2D(calcX(curr.c), calcY(curr.r)));
                curr = parentMap.get(curr);
            }
            if (!path.isEmpty()) {
                path.remove(0); // Exclude starting point itself
            }
        }

        defaultPathCache.put(start, path);
        return path;
    }

    /** Converts an explicit list of grid waypoints into pixel waypoints (excluding the spawn tile itself). */
    private List<Point2D> convertGridPathToPixels(List<GridPoint> gridPath) {
        List<Point2D> path = new ArrayList<>();
        for (int i = 1; i < gridPath.size(); i++) { // skip index 0: that's the spawn tile
            GridPoint gp = gridPath.get(i);
            path.add(new Point2D(calcX(gp.c), calcY(gp.r)));
        }
        return path;
    }

    /** Legacy/simple entry point: spawns at the map's default detected spawn tile using the default BFS route. */
    public void spawnEnemy(EnemyType type) {
        spawnEnemy(type, new GridPoint(spawnRow, spawnCol), null);
    }

    /**
     * Full spawn entry point used by wave configs.
     *
     * @param type       enemy definition to spawn
     * @param spawnPoint grid tile to spawn on; if null, uses the map's default detected spawn tile
     * @param customPath explicit grid route (first element should equal spawnPoint); if null/empty,
     *                   the default BFS route from spawnPoint to PLAYER_OBJECTIVE is used
     */
    public void spawnEnemy(EnemyType type, GridPoint spawnPoint, List<GridPoint> customPath) {
        spawnEnemy(type, spawnPoint, customPath, 1.0);
    }

    /**
     * Same as {@link #spawnEnemy(EnemyType, GridPoint, List)}, but scales the
     * spawned enemy's HP/ATK/DEF/RES by {@code statMultiplier} (1.0 = base
     * EnemyType stats). Used by endless stages to make enemies progressively
     * stronger wave after wave. Resistance is clamped to 100% since it's a
     * damage-reduction fraction, not a raw stat.
     */
    public void spawnEnemy(EnemyType type, GridPoint spawnPoint, List<GridPoint> customPath, double statMultiplier) {
        GridPoint origin = spawnPoint != null ? spawnPoint : new GridPoint(spawnRow, spawnCol);

        List<Point2D> path;
        if (customPath != null && !customPath.isEmpty()) {
            path = convertGridPathToPixels(customPath);
        } else {
            path = computeBfsPath(origin);
        }

        double mult = statMultiplier <= 0 ? 1.0 : statMultiplier;

        Enemy enemy = new Enemy(
            calcX(origin.c),
            calcY(origin.r),
            type.getHp() * mult,
            type.getAtk() * mult,
            type.getBlockCount(),
            type.getAttackType(),
            type.getAttackInterval(),
            Math.min(1.0, type.getResistance() * mult),
            type.isGround(),
            type.getDefense() * mult,
            type.getSpeed(),
            path
        );
        activeEnemies.add(enemy);

        double spriteSize = SpriteSizing.enemySize(tileWidth, tileHeight);
        EntityAnimationController animation = new EntityAnimationController(
            enemy,
            type.name(),
            true,
            type.getRadius(),
            type.getColor(),
            spriteSize,
            spriteSize
        );
        animations.put(enemy, animation);

        javafx.scene.Node enemySprite = animation.getSprite().getNode();
        enemySprite.layoutXProperty().bind(enemy.xProperty().subtract(spriteSize * 0.435));
        enemySprite.layoutYProperty().bind(enemy.yProperty().subtract(spriteSize * 0.75));
        entityLayer.track(enemySprite, enemy::getY);

        enemy.isAliveProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                // Keep the node alive until the death animation has played.
                // update() removes the entity/node after the death animation finishes.
            }
        });
    }

    /**
     * Loads a wave/config-driven spawn timeline. Once loaded, update() will
     * automatically spawn enemies at their scheduled times. Calling this
     * replaces any previously loaded schedule and resets the internal clock.
     */
    public void loadWaveConfig(WaveConfig waveConfig) {
        this.scheduledSpawns = waveConfig != null ? waveConfig.flatten() : new ArrayList<>();
        this.nextScheduledIndex = 0;
        this.elapsedSeconds = 0;
        this.waveConfigLoaded = true;
        this.stageCleared = false;
        this.infiniteWaveGenerator = null;
    }

    /**
     * Loads an endless/infinite stage: instead of a fixed, finite list of
     * waves, {@code generator} is asked to produce one more {@link Wave}
     * at a time, lazily, as the stage clock approaches needing it. The
     * stage then never reaches "wave spawning finished" / stage-cleared —
     * it simply keeps going, wave after wave, each one typically stronger
     * than the last, until the player runs out of lives.
     */
    public void loadInfiniteWaveConfig(EndlessWaveGenerator generator) {
        this.scheduledSpawns = new ArrayList<>();
        this.nextScheduledIndex = 0;
        this.elapsedSeconds = 0;
        this.waveConfigLoaded = true;
        this.stageCleared = false;
        this.infiniteWaveGenerator = generator;
        this.nextGeneratedWaveIndex = 0;
        this.infiniteWaveClock = 0;
    }

    /** Pulls one more wave out of the endless generator and queues its spawns. */
    private void generateNextInfiniteWave() {
        if (infiniteWaveGenerator == null) return;

        Wave wave = infiniteWaveGenerator.generateWave(nextGeneratedWaveIndex);
        if (wave == null) return; // defensive: a misbehaving generator shouldn't crash the run

        infiniteWaveClock += wave.getStartDelaySeconds();

        scheduledSpawns.addAll(
            WaveConfig.flattenSingleWave(wave, infiniteWaveClock, nextGeneratedWaveIndex)
        );

        nextGeneratedWaveIndex++;
    }

    /** Processes due spawns from the loaded WaveConfig (or endless generator), if any. Called once per simulated frame from update(). */
    private void processScheduledSpawns() {
        if (scheduledSpawns.isEmpty() && infiniteWaveGenerator == null) return;

        elapsedSeconds += SECONDS_PER_UPDATE;

        // Endless mode: keep a rolling window of already-generated spawns
        // ahead of the clock, generating more waves as needed. This is what
        // makes the stage effectively infinite without ever materializing
        // more than a small lookahead window in memory.
        while (infiniteWaveGenerator != null
                && (scheduledSpawns.isEmpty()
                    || scheduledSpawns.get(scheduledSpawns.size() - 1).absoluteTimeSeconds
                            <= elapsedSeconds + INFINITE_LOOKAHEAD_SECONDS)) {
            generateNextInfiniteWave();
        }

        while (nextScheduledIndex < scheduledSpawns.size()
                && scheduledSpawns.get(nextScheduledIndex).absoluteTimeSeconds <= elapsedSeconds) {

            WaveConfig.ScheduledSpawn scheduled = scheduledSpawns.get(nextScheduledIndex);
            SpawnEntry entry = scheduled.entry;

            GridPoint spawnPoint = entry.hasCustomSpawnPoint() ? entry.getSpawnPoint() : null;
            List<GridPoint> customPath = entry.hasCustomPath() ? entry.getCustomPath() : null;

            spawnEnemy(entry.getEnemyType(), spawnPoint, customPath, entry.getStatMultiplier());
            nextScheduledIndex++;
        }

        // Bound memory for very long endless runs: forget spawns we've
        // already dispatched once there's a healthy backlog of them.
        if (infiniteWaveGenerator != null && nextScheduledIndex > INFINITE_TRIM_THRESHOLD) {
            scheduledSpawns.subList(0, nextScheduledIndex).clear();
            nextScheduledIndex = 0;
        }
    }

    /**
     * True once every scheduled spawn from the loaded WaveConfig has been
     * dispatched. Endless stages never finish spawning by definition, so
     * this always returns false while an EndlessWaveGenerator is active —
     * which in turn means {@link #checkStageClear()} never fires for them;
     * the run only ends via {@link #loseLife()} reaching zero lives.
     */
    public boolean isWaveSpawningFinished() {
        if (infiniteWaveGenerator != null) return false;
        return scheduledSpawns.isEmpty() || nextScheduledIndex >= scheduledSpawns.size();
    }

    /** True if the currently loaded stage is an endless/infinite one. */
    public boolean isInfiniteStage() {
        return infiniteWaveGenerator != null;
    }

    public void update() {
        if (gameOver) return;

        processScheduledSpawns();

        var iterator = activeEnemies.iterator();
        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            EntityAnimationController animation = animations.get(enemy);

            boolean reachedObjective = enemy.isAlive()
                    && enemy.getWaypoints() != null
                    && enemy.getCurrentWaypointIndex() >= enemy.getWaypoints().size();

            if (reachedObjective) {
                // Enemy leaked through to the objective: remove immediately
                // (no death animation - it wasn't killed, it got past us)
                // and dock a life, mirroring the same iterator.remove()
                // pattern used below for enemies that die in combat.
                if (animation != null) {
                    entityLayer.untrack(animation.getSprite().getNode());
                }
                animations.remove(enemy);
                iterator.remove();

                loseLife();
                continue;
            }

            if (enemy.isAlive()) {
                enemy.update();
                enemy.updateGridPosition(
                    offsetX,
                    offsetY,
                    tileWidth,
                    tileHeight,
                    paddingX,
                    paddingY
                );
            }

            if (animation != null) {
                if (enemy.consumeAttackTriggered()) animation.triggerAttack();
                animation.update();
            }

            if (!enemy.isAlive()
                    && (animation == null
                        || animation.getSprite().isCurrentAnimationFinished())) {

                if (animation != null) {
                    entityLayer.untrack(animation.getSprite().getNode());
                }

                animations.remove(enemy);
                iterator.remove();

                // Actually killed (not leaked through) -> reward crystals.
                PlayerProgress.addCrystals(CRYSTALS_PER_KILL);
            }

            if (gameOver) break; // stop processing remaining enemies once defeat triggers this frame
        }

        checkStageClear();
    }

    /** Fires the stage-clear callback once, the moment every scheduled wave has spawned and the field is empty. */
    private void checkStageClear() {
        if (gameOver || stageCleared || !waveConfigLoaded) return;

        if (isWaveSpawningFinished() && activeEnemies.isEmpty()) {
            stageCleared = true;
            if (onStageClearCallback != null) {
                onStageClearCallback.run();
            }
        }
    }

    public boolean isStageCleared() { return stageCleared; }

    /** Registers a callback fired exactly once, the moment the stage is cleared (all waves spawned + field empty). */
    public void setOnStageClear(Runnable callback) { this.onStageClearCallback = callback; }

    /** Deducts one base life. Fires the game-over callback once lives reach zero. */
    private void loseLife() {
        if (gameOver) return;

        lives = Math.max(0, lives - 1);
        if (onLivesChangedCallback != null) {
            onLivesChangedCallback.accept(lives);
        }

        if (lives <= 0) {
            gameOver = true;
            if (onGameOverCallback != null) {
                onGameOverCallback.run();
            }
        }
    }

    public int getLives() { return lives; }
    public boolean isGameOver() { return gameOver; }

    /** Registers a callback fired exactly once, the moment lives reach zero. */
    public void setOnGameOver(Runnable callback) { this.onGameOverCallback = callback; }

    /** Registers a callback fired every time a life is lost, with the remaining life count. */
    public void setOnLivesChanged(IntConsumer callback) { this.onLivesChangedCallback = callback; }

    public List<Enemy> getActiveEnemies() {
        return activeEnemies;
    }

    // Dynamic pixel position formulas aligned to tile centers
    private double calcX(int col) { return offsetX + (col * (tileWidth + paddingX)) + (tileWidth / 2.0); }
    private double calcY(int row) { return offsetY + (row * (tileHeight + paddingY)) + (tileHeight / 2.0); }
}