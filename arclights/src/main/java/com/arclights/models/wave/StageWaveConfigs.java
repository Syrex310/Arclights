package com.arclights.models.wave;

import java.util.Arrays;
import java.util.List;

import com.arclights.entity.enemy.EnemyType;
import com.arclights.models.MapPresets;

public class StageWaveConfigs {

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
            .label("Wave 0")
            .startDelay(10.0)
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 0))
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 1))
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 2))
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 3))
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 4))
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 5))
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 6))
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 7))
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 8))
            .build(),
        Wave.builder()
            .label("Wave 1")
            .startDelay(10.0)
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 0))
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 1))
            .spawn(SpawnEntry.of(EnemyType.HOUND, 2))
            .spawn(SpawnEntry.of(EnemyType.HOUND, 3))
            .spawn(SpawnEntry.of(EnemyType.HOUND, 6))
            .spawn(SpawnEntry.of(EnemyType.HOUND, 7))
            .build(),
        Wave.builder()
            .label("Wave 2")
            .startDelay(15.0)
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 0))
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 1))
            .spawn(SpawnEntry.of(EnemyType.HOUND, 2))
            .spawn(SpawnEntry.of(EnemyType.SOLDIER, 1))
            .spawn(SpawnEntry.of(EnemyType.SOLDIER, 1.5))
            .build(),
        Wave.builder()
            .label("Wave 3")
            .startDelay(15.0)
            .spawn(SpawnEntry.of(EnemyType.HOUND, 0))
            .spawn(SpawnEntry.of(EnemyType.HOUND, 1))
            .spawn(SpawnEntry.of(EnemyType.HOUND, 2))
            .spawn(SpawnEntry.of(EnemyType.HOUND, 3))
            .spawn(SpawnEntry.of(EnemyType.SOLDIER, 4))
            .spawn(SpawnEntry.of(EnemyType.SOLDIER, 5))
            .spawn(SpawnEntry.of(EnemyType.SOLDIER, 9))
            .spawn(SpawnEntry.of(EnemyType.SOLDIER, 10))
            .build()
    );

    private static final WaveConfig STAGE_1_3_CONFIG = WaveConfig.of(
        Wave.builder()
            .label("Wave 1")
            .startDelay(10)
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 0))
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 1))
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 2))
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 3))
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 4))
            .spawn(SpawnEntry.of(EnemyType.HOUND, 6.5))
            .spawn(SpawnEntry.of(EnemyType.HOUND, 9))
            .build(),
        Wave.builder()
            .label("Wave 2")
            .startDelay(15)
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.HOUND, 0, 0, 4))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.HOUND, 1, 0, 4))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.HOUND, 2, 0, 4))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.SOLDIER, 5, 0, 4))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.SOLDIER, 6, 0, 4))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.SOLDIER, 7, 0, 4))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.SOLDIER, 10, 0, 4))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.SOLDIER, 12, 0, 4))
            .build(),
        Wave.builder()
            .label("Wave 3")
            .startDelay(10)
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 0))
            .spawn(SpawnEntry.of(EnemyType.ORIGINIUM_SLUG, 1))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.HOUND, 2, 0, 4))
            .spawn(SpawnEntry.of(EnemyType.HOUND, 3))
            .spawn(SpawnEntry.of(EnemyType.SOLDIER, 4))
            .spawn(SpawnEntry.of(EnemyType.SOLDIER, 5))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.SOLDIER, 5, 0, 4))
            .spawn(SpawnEntry.of(EnemyType.BIG_BOB, 6))
            .spawn(SpawnEntry.of(EnemyType.SOLDIER, 9))
            .spawn(SpawnEntry.of(EnemyType.SOLDIER, 10))
            .build()
    );

    private static final double STAGE_1_4_GROWTH_PER_WAVE = 0.10; // +10% per wave, compounding

    private static final List<Wave> STAGE_1_4_TEMPLATE_WAVES = Arrays.asList(
        Wave.builder()
            .label("S1")
            .startDelay(10)
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.ORIGINIUM_SLUG, 0, 0, 1))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.ORIGINIUM_SLUG, 1, 0, 1))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.HOUND, 4, 0, 1))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.HOUND, 5, 0, 1))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.SOLDIER, 8, 0, 1))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.SOLDIER, 9, 0, 1))
            .build(),
        Wave.builder()
            .label("S2")
            .startDelay(10)
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.ORIGINIUM_SLUG, 0, 4, 0))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.ORIGINIUM_SLUG, 1, 4, 0))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.ORIGINIUM_SLUG, 2, 4, 0))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.ORIGINIUM_SLUG, 3, 4, 0))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.ORIGINIUM_SLUG, 4, 5, 0))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.ORIGINIUM_SLUG, 5, 5, 0))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.ORIGINIUM_SLUG, 6, 5, 0))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.ORIGINIUM_SLUG, 7, 5, 0))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.HOUND, 9, 4, 0))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.HOUND, 10, 5, 0))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.SOLDIER, 14, 4, 0))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.SOLDIER, 15, 5, 0))
            .build(),
        Wave.builder()
            .label("S3")
            .startDelay(10)
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.ORIGINIUM_SLUG, 0, 0, 1))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.HOUND, 4, 0, 1))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.SOLDIER, 8, 0, 1))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.SOLDIER, 9, 0, 1))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.ORIGINIUM_SLUG, 2, 5, 0))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.ORIGINIUM_SLUG, 3, 5, 0))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.HOUND, 6, 4, 0))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.HOUND, 7, 5, 0))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.SOLDIER, 11, 4, 0))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.SOLDIER, 12, 5, 0))
            .spawn(SpawnEntry.atSpawnPoint(EnemyType.BIG_BOB, 18, 4, 0))
            .build()
    );


    public static final EndlessWaveGenerator STAGE_1_4_ENDLESS_GENERATOR = waveIndex -> {
        Wave template = STAGE_1_4_TEMPLATE_WAVES.get(waveIndex % STAGE_1_4_TEMPLATE_WAVES.size());
        double statMultiplier = Math.pow(1.0 + STAGE_1_4_GROWTH_PER_WAVE, waveIndex);

        Wave.Builder builder = Wave.builder()
            .label(String.format("Wave %d - %s (x%.2f)", waveIndex + 1, template.getLabel(), statMultiplier))
            .startDelay(template.getStartDelaySeconds());

        for (SpawnEntry entry : template.getSpawns()) {
            builder.spawn(entry.withStatMultiplier(statMultiplier));
        }

        return builder.build();
    };

    /** True if this layout's stage is endless (see {@link #getInfiniteGeneratorForLayout}) rather than a finite WaveConfig. */
    public static boolean isInfinite(char[][] layout) {
        return layout == MapPresets.LEVEL_4_LAYOUT;
    }

    /** Endless wave generator for infinite stages, or null if this layout uses a normal finite WaveConfig. */
    public static EndlessWaveGenerator getInfiniteGeneratorForLayout(char[][] layout) {
        if (layout == MapPresets.LEVEL_4_LAYOUT) return STAGE_1_4_ENDLESS_GENERATOR;
        return null;
    }

    public static WaveConfig getConfigForLayout(char[][] layout) {
        if (layout == MapPresets.LEVEL_1_LAYOUT) return STAGE_1_1_CONFIG;
        if (layout == MapPresets.LEVEL_2_LAYOUT) return DEFAULT_CONFIG;
        if (layout == MapPresets.LEVEL_3_LAYOUT) return STAGE_1_3_CONFIG;
        // LEVEL_4_LAYOUT is endless -> handled via isInfinite()/getInfiniteGeneratorForLayout()
        // + EnemyManager.loadInfiniteWaveConfig() instead of a finite WaveConfig.
        return DEFAULT_CONFIG;
    }
}