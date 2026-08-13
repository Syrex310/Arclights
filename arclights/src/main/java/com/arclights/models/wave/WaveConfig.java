package com.arclights.models.wave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Full wave timeline for a stage: an ordered list of {@link Wave}s.
 *
 * Use {@link #flatten()} to obtain every {@link SpawnEntry} paired with its
 * absolute spawn time (seconds since stage start) — this is what
 * EnemyManager actually schedules against.
 */
public class WaveConfig {
    private final List<Wave> waves;

    public WaveConfig(List<Wave> waves) {
        this.waves = waves == null ? new ArrayList<>() : new ArrayList<>(waves);
    }

    public static WaveConfig of(Wave... waves) {
        List<Wave> list = new ArrayList<>();
        Collections.addAll(list, waves);
        return new WaveConfig(list);
    }

    public List<Wave> getWaves() { return Collections.unmodifiableList(waves); }

    /** One flattened, time-ordered spawn instruction ready for scheduling. */
    public static class ScheduledSpawn {
        public final double absoluteTimeSeconds;
        public final SpawnEntry entry;
        public final int waveIndex;

        ScheduledSpawn(double absoluteTimeSeconds, SpawnEntry entry, int waveIndex) {
            this.absoluteTimeSeconds = absoluteTimeSeconds;
            this.entry = entry;
            this.waveIndex = waveIndex;
        }
    }

    /**
     * Flattens all waves into a single time-sorted list of scheduled spawns.
     * Wave start times accumulate: wave N begins `startDelaySeconds` after
     * wave N-1 begins (not after it ends/clears).
     */
    public List<ScheduledSpawn> flatten() {
        List<ScheduledSpawn> result = new ArrayList<>();
        double waveStartTime = 0;

        for (int i = 0; i < waves.size(); i++) {
            Wave wave = waves.get(i);
            waveStartTime += wave.getStartDelaySeconds();

            for (SpawnEntry entry : wave.getSpawns()) {
                double absoluteTime = waveStartTime + entry.getDelaySeconds();
                result.add(new ScheduledSpawn(absoluteTime, entry, i));
            }
        }

        result.sort((a, b) -> Double.compare(a.absoluteTimeSeconds, b.absoluteTimeSeconds));
        return result;
    }

    public boolean isEmpty() { return waves.isEmpty(); }
}
