package com.arclights.animation;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.scene.image.Image;

public final class SpriteAnimation {
    // Cache decoded frames per resource directory so every spawned entity of the
    // same type/state reuses the same in-memory Images instead of re-decoding the
    // PNGs from disk on every spawn. This is what was causing the heap to blow up
    // (OutOfMemoryError) after spawning a number of enemies/operators.
    private static final Map<String, List<Image>> FRAME_CACHE = new HashMap<>();

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

    /**
     * Backwards-compatible overload: decodes frames at full resolution.
     * Prefer {@link #load(String, double, boolean, double, double)} so large
     * source PNGs (e.g. 1000x1000) don't get decoded at full size just to be
     * displayed at a fraction of that on screen.
     */
    public static SpriteAnimation load(String resourceDirectory, double ticksPerFrame, boolean loop) {
        return load(resourceDirectory, ticksPerFrame, loop, 0, 0);
    }

    /**
     * @param requestedWidth  target decode width in px (0 = keep original size)
     * @param requestedHeight target decode height in px (0 = keep original size)
     */
    public static SpriteAnimation load(String resourceDirectory, double ticksPerFrame, boolean loop,
                                        double requestedWidth, double requestedHeight) {
        String dir = resourceDirectory.endsWith("/") ? resourceDirectory : resourceDirectory + "/";
        // Include the target size in the cache key: the same sprite sheet could
        // theoretically be requested at different display sizes.
        String cacheKey = dir + "|" + (int) requestedWidth + "x" + (int) requestedHeight;

        List<Image> frames = FRAME_CACHE.get(cacheKey);
        if (frames == null) {
            frames = loadFramesFromDisk(dir, requestedWidth, requestedHeight);
            FRAME_CACHE.put(cacheKey, frames);
        }

        return new SpriteAnimation(frames, ticksPerFrame, loop);
    }

    private static List<Image> loadFramesFromDisk(String dir, double requestedWidth, double requestedHeight) {
        List<Image> frames = new ArrayList<>();

        int misses = 0;
        for (int i = 0; i < 1000 && misses < 8; i++) {
            String resource = dir + i + ".png";
            try (InputStream stream = SpriteAnimation.class.getResourceAsStream(resource)) {
                if (stream != null) {
                    // Decoding at the requested display size (instead of full
                    // resolution) is what actually fixes the OutOfMemoryError:
                    // a 1000x1000 PNG decoded at full size costs ~4MB per frame,
                    // while decoding straight to e.g. 96x96 costs a fraction of
                    // that. preserveRatio=true keeps aspect ratio, smooth=true
                    // avoids a blocky/pixelated downscale.
                    System.out.println("[DEBUG Decode] File: " + resource + " | Size: " + requestedWidth + "x" + requestedHeight);
                    frames.add(new Image(stream, requestedWidth, requestedHeight, true, true));
                    misses = 0;
                } else {
                    misses++;
                }
            } catch (Exception ignored) {
                misses++;
            }
        }

        // Frames are shared/reused across every SpriteAnimation instance built from
        // this directory, so make sure nobody can mutate the cached list.
        return Collections.unmodifiableList(frames);
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