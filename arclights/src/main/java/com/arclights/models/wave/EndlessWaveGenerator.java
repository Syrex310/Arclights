package com.arclights.models.wave;


@FunctionalInterface
public interface EndlessWaveGenerator {
    Wave generateWave(int waveIndex);
}