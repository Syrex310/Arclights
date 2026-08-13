package com.arclights.models.wave;

import com.arclights.entity.enemy.EnemyType;
import com.arclights.models.GridPoint;
import com.arclights.models.MapPresets;

import java.util.Arrays;

/**
 * Per-stage wave definitions, keyed off the same char[][] layout instances
 * used by MapPresets. Add a new stage's timeline here the same way
 * MapPresets registers a new map layout.
 */
public class StageWaveConfigs {

    // Legacy behaviour preserved: same 4 enemies that used to be hardcoded
    // in App.java, now expressed as a single wave with no delay so nothing
    // changes gameplay-wise for stages that don't define their own timeline.
    private static final WaveConfig DEFAULT_CONFIG = WaveConfig.of(
        Wave.builder()
            .label("Wave 1")
            .startDelay(0)
            .spawn(SpawnEntry.of(EnemyType.BIG_BOB, 0))
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 0.5))
            .spawn(SpawnEntry.of(EnemyType.HOUND, 1.0))
            .spawn(SpawnEntry.of(EnemyType.SOLDIER, 1.5))
            .build()
    );

    private static final WaveConfig STAGE_1_1_CONFIG = WaveConfig.of(
        Wave.builder()
            .label("Wave 1")
            .startDelay(1.0)
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 0))
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 1.0))
            .spawn(SpawnEntry.of(EnemyType.HOUND, 2.5))
            .build(),
        Wave.builder()
            .label("Wave 2")
            .startDelay(8.0) // relative to wave 1's start
            .spawn(SpawnEntry.of(EnemyType.SOLDIER, 0))
            .spawn(SpawnEntry.of(EnemyType.SOLDIER, 1.5))
            .spawn(SpawnEntry.of(EnemyType.HOUND, 3.0))
            .build(),
        Wave.builder()
            .label("Wave 3 - Boss")
            .startDelay(8.0)
            .spawn(SpawnEntry.of(EnemyType.BIG_BOB, 0))
            .spawn(SpawnEntry.of(EnemyType.SOLDIER, 1.0))
            .spawn(SpawnEntry.of(EnemyType.SOLDIER, 2.0))
            .build()
    );

    private static final WaveConfig STAGE_1_2_CONFIG = WaveConfig.of(
        Wave.builder()
            .label("Wave 1")
            .startDelay(1.0)
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 0))
            .spawn(SpawnEntry.of(EnemyType.HOUND, 1.5))
            .build(),
        Wave.builder()
            .label("Wave 2")
            .startDelay(6.0)
            .spawn(SpawnEntry.of(EnemyType.SOLDIER, 0))
            .spawn(SpawnEntry.of(EnemyType.SOLDIER, 1.0))
            .spawn(SpawnEntry.of(EnemyType.HOUND, 2.0))
            .build()
    );

    // Stage 1-3 has two 'S' spawn tiles (top at row0,col4 and left at row3,col0).
    // Demonstrates: per-entry custom spawn point, and a fully custom hand-drawn
    // route (grid waypoints) instead of the default BFS path.
    private static final WaveConfig STAGE_1_3_CONFIG = WaveConfig.of(
        Wave.builder()
            .label("Wave 1 - North gate")
            .startDelay(1.0)
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.ORIGINIUM_SLUG, 0, 0, 4))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.ORIGINIUM_SLUG, 1.0, 0, 4))
            .build(),
        Wave.builder()
            .label("Wave 2 - West gate")
            .startDelay(6.0)
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.SOLDIER, 0, 3, 0))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.HOUND, 1.5, 3, 0))
            .build(),
        Wave.builder()
            .label("Wave 3 - Custom route rush")
            .startDelay(6.0)
            // Explicit hand-authored route from the north gate straight down
            // the middle column instead of the shortest BFS path.
            .spawn(SpawnEntry.withCustomPath(EnemyType.HOUND, 0, Arrays.asList(
                new GridPoint(0, 4),
                new GridPoint(1, 4),
                new GridPoint(2, 4),
                new GridPoint(3, 4),
                new GridPoint(4, 4),
                new GridPoint(5, 5),
                new GridPoint(6, 5)
            )))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.BIG_BOB, 2.0, 3, 0))
            .build()
    );

    public static WaveConfig getConfigForLayout(char[][] layout) {
        if (layout == MapPresets.LEVEL_1_LAYOUT) return STAGE_1_1_CONFIG;
        if (layout == MapPresets.LEVEL_2_LAYOUT) return STAGE_1_2_CONFIG;
        if (layout == MapPresets.LEVEL_3_LAYOUT) return STAGE_1_3_CONFIG;
        return DEFAULT_CONFIG;
    }
}
