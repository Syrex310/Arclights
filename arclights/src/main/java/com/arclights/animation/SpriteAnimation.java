package com.arclights.animation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;

public final class SpriteAnimation {
    private final List<Image> frames = new ArrayList<>();
    private final double ticksPerFrame;
    private final boolean loop;
    private int frameIndex;
    private double tickCounter;
    private boolean finished;

    private SpriteAnimation(List<Image> frames, double ticksPerFrame, boolean loop) {
        this.frames.addAll(frames);
        this.ticksPerFrame = Math.max(1, ticksPerFrame);
        this.loop = loop;
    }

    public static SpriteAnimation load(String resourceDirectory, double ticksPerFrame, boolean loop) {
        List<Image> frames = new ArrayList<>();
        String dir = resourceDirectory.endsWith("/") ? resourceDirectory : resourceDirectory + "/";


        int misses = 0;
        for (int i = 0; i < 1000 && misses < 8; i++) {
            String resource = dir + i + ".png";
            try (InputStream stream = SpriteAnimation.class.getResourceAsStream(resource)) {
                if (stream != null) {
                    frames.add(new Image(stream));
                    misses = 0;
                } else {
                    misses++;
                }
            } catch (Exception ignored) {
                misses++;
            }
        }

        return new SpriteAnimation(frames, ticksPerFrame, loop);
    }

    public static SpriteAnimation fromImages(List<Image> images, int ticksPerFrame, boolean loop) {
        return new SpriteAnimation(images, ticksPerFrame, loop);
    }

    public boolean hasFrames() { return !frames.isEmpty(); }
    public Image getCurrentFrame() { return frames.isEmpty() ? null : frames.get(frameIndex); }
    public int getFrameIndex() { return frameIndex; }
    public boolean isFinished() { return finished; }
    public int getFrameCount() { return frames.size(); }

    public void reset() {
        frameIndex = 0;
        tickCounter = 0;
        finished = false;
    }

    public void update() {
        if (frames.size() <= 1 || finished) return;
        tickCounter++;
        if (tickCounter < ticksPerFrame) return;
        tickCounter = 0;
        frameIndex++;
        if (frameIndex >= frames.size()) {
            if (loop) frameIndex = 0;
            else {
                frameIndex = frames.size() - 1;
                finished = true;
            }
        }
    }
}
