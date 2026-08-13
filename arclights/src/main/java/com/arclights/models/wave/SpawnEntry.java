package com.arclights.models.wave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.arclights.entity.enemy.EnemyType;
import com.arclights.models.GridPoint;

/**
 * A single "spawn this enemy" instruction inside a {@link Wave}.
 *
 * By default, an entry only needs an {@link EnemyType} and a delay (seconds
 * after the wave itself starts). The enemy will spawn at the map's detected
 * ENEMY_SPAWN tile and walk the default BFS route to PLAYER_OBJECTIVE
 * (EnemyManager already implements this pathfinding).
 *
 * For custom scenarios, an entry can optionally override:
 *  - spawnPoint: which grid tile the enemy appears on (useful for maps with
 *    multiple 'S' spawn tiles).
 *  - customPath: an explicit list of grid waypoints the enemy should follow
 *    instead of the auto-computed BFS route.
 */
public class SpawnEntry {
    private final EnemyType enemyType;
    private final double delaySeconds;
    private final GridPoint spawnPoint; // nullable -> use map's default spawn tile
    private final List<GridPoint> customPath; // nullable/empty -> use default BFS path

    public SpawnEntry(EnemyType enemyType, double delaySeconds) {
        this(enemyType, delaySeconds, null, null);
    }

    public SpawnEntry(EnemyType enemyType, double delaySeconds, GridPoint spawnPoint) {
        this(enemyType, delaySeconds, spawnPoint, null);
    }

    public SpawnEntry(EnemyType enemyType, double delaySeconds, GridPoint spawnPoint, List<GridPoint> customPath) {
        if (enemyType == null) {
            throw new IllegalArgumentException("enemyType cannot be null");
        }
        this.enemyType = enemyType;
        this.delaySeconds = Math.max(0, delaySeconds);
        this.spawnPoint = spawnPoint;
        this.customPath = customPath == null ? Collections.emptyList() : new ArrayList<>(customPath);
    }

    /** Convenience factory for the common "just spawn N seconds in" case. */
    public static SpawnEntry of(EnemyType type, double delaySeconds) {
        return new SpawnEntry(type, delaySeconds);
    }

    /** Convenience factory for a custom spawn tile with default (BFS) routing. */
    public static SpawnEntry atSpawnPoint(EnemyType type, double delaySeconds, int spawnRow, int spawnCol) {
        return new SpawnEntry(type, delaySeconds, new GridPoint(spawnRow, spawnCol));
    }

    /** Convenience factory for a fully custom route (list of {row, col} pairs). */
    public static SpawnEntry withCustomPath(EnemyType type, double delaySeconds, List<GridPoint> path) {
        GridPoint start = (path != null && !path.isEmpty()) ? path.get(0) : null;
        return new SpawnEntry(type, delaySeconds, start, path);
    }

    public EnemyType getEnemyType() { return enemyType; }
    public double getDelaySeconds() { return delaySeconds; }
    public GridPoint getSpawnPoint() { return spawnPoint; }
    public boolean hasCustomSpawnPoint() { return spawnPoint != null; }
    public List<GridPoint> getCustomPath() { return customPath; }
    public boolean hasCustomPath() { return customPath != null && !customPath.isEmpty(); }
}
