package com.arclights.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.arclights.animation.EntityAnimationController;
import com.arclights.entity.enemy.Enemy;
import com.arclights.entity.enemy.EnemyType;
import com.arclights.models.GameMap;
import com.arclights.models.Tile;
import com.arclights.ui.MapRenderer;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;


public class EnemyManager {
    private final List<Enemy> activeEnemies = new ArrayList<>();
    private final Map<Enemy, EntityAnimationController> animations = new HashMap<>();
    private final List<Point2D> enemyPath = new ArrayList<>();
    private final Pane root;
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

    private static class GridPoint {
        final int r, c;
        GridPoint(int r, int c) { this.r = r; this.c = c; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof GridPoint)) return false;
            GridPoint that = (GridPoint) o;
            return r == that.r && c == that.c;
        }
        @Override
        public int hashCode() {
            return java.util.Objects.hash(r, c);
        }
    }

    // Constructor updated to accept render dimensions
    public EnemyManager(Pane root, GameMap gameMap, MapRenderer.RenderResult renderResult) {
        this.root = root;
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
    public EnemyManager(Pane root, GameMap gameMap, double tileWidth, double tileHeight, double paddingX, double paddingY, double offsetX, double offsetY) {
        this.root = root;
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
        int targetRow = -1;
        int targetCol = -1;

        for (int r = 0; r < gameMap.getRows(); r++) {
            for (int c = 0; c < gameMap.getCols(); c++) {
                Tile tile = gameMap.getTile(r, c);
                if (tile.getTileType() == Tile.TileType.ENEMY_SPAWN) {
                    detectedSpawnRow = r;
                    detectedSpawnCol = c;
                } else if (tile.getTileType() == Tile.TileType.PLAYER_OBJECTIVE) {
                    targetRow = r;
                    targetCol = c;
                }
            }
        }

        if (detectedSpawnRow != -1 && detectedSpawnCol != -1) {
            this.spawnRow = detectedSpawnRow;
            this.spawnCol = detectedSpawnCol;
        }

        if (spawnRow == -1 || spawnCol == -1 || targetRow == -1 || targetCol == -1) {
            this.spawnRow = 1;
            this.spawnCol = 0;
            targetRow = 3;
            targetCol = 7;
        }

        // Pathfinding: (spawnRow, spawnCol) -> (targetRow, targetCol)
        Queue<GridPoint> queue = new LinkedList<>();
        Set<GridPoint> visited = new HashSet<>();
        Map<GridPoint, GridPoint> parentMap = new HashMap<>();

        GridPoint start = new GridPoint(spawnRow, spawnCol);
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

        if (found) {
            List<Point2D> path = new ArrayList<>();
            GridPoint curr = target;
            while (curr != null) {
                path.add(0, new Point2D(calcX(curr.c), calcY(curr.r)));
                curr = parentMap.get(curr);
            }
            if (!path.isEmpty()) {
                path.remove(0); // Exclude starting point itself
            }
            enemyPath.addAll(path);
        }
    }

    public void spawnEnemy(EnemyType type) {
        Enemy enemy = new Enemy(
            calcX(spawnCol), 
            calcY(spawnRow), 
            type.getHp(),
            type.getAtk(),
            type.getBlockCount(),
            type.getAttackType(),
            type.getAttackInterval(),
            type.getResistance(),
            type.isGround(),
            type.getDefense(),
            type.getSpeed(),
            enemyPath
        );
        activeEnemies.add(enemy);

        double spriteSize = Math.min(tileWidth, tileHeight) * 1.7;
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
        enemySprite.layoutXProperty().bind(enemy.xProperty().subtract(spriteSize * 0.5));
        enemySprite.layoutYProperty().bind(enemy.yProperty().subtract(spriteSize * 0.75));
        root.getChildren().add(enemySprite);

        enemy.isAliveProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                // Keep the node alive until the death animation has played.
                // update() removes the entity/node after the death animation finishes.
            }
        });
    }

    public void update() {
        for (Enemy enemy : activeEnemies) {
            EntityAnimationController animation = animations.get(enemy);

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
        }

        var iterator = activeEnemies.iterator();

        while (iterator.hasNext()) {
            Enemy enemy = iterator.next();
            EntityAnimationController animation = animations.get(enemy);

            if (!enemy.isAlive()
                    && (animation == null
                        || animation.getSprite().isCurrentAnimationFinished())) {

                if (animation != null) {
                    root.getChildren().remove(animation.getSprite().getNode());
                }

                animations.remove(enemy);
                iterator.remove();
            }
        }
    }

    public List<Enemy> getActiveEnemies() {
        return activeEnemies;
    }

    // Dynamic pixel position formulas aligned to tile centers
    private double calcX(int col) { return offsetX + (col * (tileWidth + paddingX)) + (tileWidth / 2.0); }
    private double calcY(int row) { return offsetY + (row * (tileHeight + paddingY)) + (tileHeight / 2.0); }
}