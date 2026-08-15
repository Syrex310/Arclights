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

    /**
     * Flattens a single wave into scheduled spawns, given the absolute clock
     * time (seconds since stage start) at which that wave itself begins.
     *
     * This is the single-wave counterpart to {@link #flatten()}, used by
     * {@link EndlessWaveGenerator}-driven endless stages: since there's no
     * fixed, finite list of waves to flatten up front, EnemyManager instead
     * calls this once per generated wave, right before it's needed, and
     * keeps a running {@code waveStartTime} clock across calls (mirroring
     * the accumulation {@link #flatten()} does internally for a normal,
     * finite {@link WaveConfig}).
     */
    public static List<ScheduledSpawn> flattenSingleWave(Wave wave, double waveStartTime, int waveIndex) {
        List<ScheduledSpawn> result = new ArrayList<>();

        for (SpawnEntry entry : wave.getSpawns()) {
            double absoluteTime = waveStartTime + entry.getDelaySeconds();
            result.add(new ScheduledSpawn(absoluteTime, entry, waveIndex));
        }

        // Same as flatten(): callers (EnemyManager) walk this list strictly
        // in order via a single forward index, so it must be time-sorted
        // even if the wave's own SpawnEntry list wasn't authored in
        // ascending-delay order (e.g. entries interleaved across multiple
        // spawn points, like Stage 1-4's S3 template).
        result.sort((a, b) -> Double.compare(a.absoluteTimeSeconds, b.absoluteTimeSeconds));
        return result;
    }
}