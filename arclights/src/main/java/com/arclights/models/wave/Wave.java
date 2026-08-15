package com.arclights.models.wave;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A single wave of enemies.
 *
 * `startDelaySeconds` is measured from the moment the *previous* wave began
 * (or from the start of the stage, for the first wave). Each wave's own
 * {@link SpawnEntry} list then schedules individual enemies relative to
 * that wave's start time via SpawnEntry#getDelaySeconds().
 *
 * This mirrors the classic tower-defense "wave timeline" pattern: waves
 * keep coming on their own schedule regardless of whether the previous
 * wave has been fully dealt with.
 */
public class Wave {
    private final double startDelaySeconds;
    private final List<SpawnEntry> spawns;
    private final String label;

    public Wave(double startDelaySeconds, List<SpawnEntry> spawns) {
        this(startDelaySeconds, spawns, null);
    }

    public Wave(double startDelaySeconds, List<SpawnEntry> spawns, String label) {
        this.startDelaySeconds = Math.max(0, startDelaySeconds);
        this.spawns = spawns == null ? new ArrayList<>() : new ArrayList<>(spawns);
        this.label = label;
    }

    public double getStartDelaySeconds() { return startDelaySeconds; }
    public List<SpawnEntry> getSpawns() { return Collections.unmodifiableList(spawns); }
    public String getLabel() { return label; }

    /** Fluent builder, handy for defining waves inline without list boilerplate. */
    public static class Builder {
        private double startDelaySeconds = 0;
        private String label;
        private final List<SpawnEntry> spawns = new ArrayList<>();

        public Builder startDelay(double seconds) {
            this.startDelaySeconds = seconds;
            return this;
        }

        public Builder label(String label) {
            this.label = label;
            return this;
        }

        public Builder spawn(SpawnEntry entry) {
            this.spawns.add(entry);
            return this;
        }

        public Wave build() {
            return new Wave(startDelaySeconds, spawns, label);
        }
    }

    public static Builder builder() {
        return new Builder();
    }
}
