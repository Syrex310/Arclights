package com.arclights.models;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * App-wide player progress tracker: crystal currency + which stages have
 * been cleared before (for first-clear vs repeat-clear rewards).
 *
 * Persisted to a small properties file under the user's home directory
 * (~/.arclights/save.properties), loaded once on class init and re-saved
 * every time progress changes. crystalsProperty() is a real JavaFX
 * property so any screen (e.g. StartMenu) can bind a Label's text to it
 * and get live updates for free.
 */
public class PlayerProgress {

    private static final Path SAVE_DIR = Paths.get(System.getProperty("user.home"), ".arclights");
    private static final Path SAVE_FILE = SAVE_DIR.resolve("save.properties");

    // Stage names (used as save keys) are joined with this delimiter when
    // written to the properties file. None of the current stage display
    // names ("1-1 Main Corridor", etc.) contain it.
    private static final String CLEARED_STAGES_DELIMITER = "|";

    private static final IntegerProperty crystals = new SimpleIntegerProperty(0);
    private static final Set<String> clearedStages = new HashSet<>();

    static {
        load();
    }

    private PlayerProgress() {
        // static-only utility class
    }

    public static IntegerProperty crystalsProperty() { return crystals; }
    public static int getCrystals() { return crystals.get(); }

    public static void addCrystals(int amount) {
        if (amount == 0) return;
        crystals.set(Math.max(0, crystals.get() + amount));
        save();
    }

    /** True if this stage (keyed by its display name, e.g. "1-1 Main Corridor") has never been cleared. */
    public static boolean isFirstClear(String stageId) {
        return stageId != null && !clearedStages.contains(stageId);
    }

    public static boolean hasCleared(String stageId) {
        return stageId != null && clearedStages.contains(stageId);
    }

    /** Marks a stage as cleared. Idempotent; only writes to disk if this actually changed something. */
    public static void markCleared(String stageId) {
        if (stageId != null && clearedStages.add(stageId)) {
            save();
        }
    }

    /** Loads saved progress from disk, if a save file exists. Silently falls back to defaults on any failure. */
    private static void load() {
        if (!Files.isReadable(SAVE_FILE)) return;

        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(SAVE_FILE)) {
            props.load(in);
        } catch (IOException e) {
            System.err.println("PlayerProgress: failed to read save file, starting fresh (" + e.getMessage() + ")");
            return;
        }

        try {
            crystals.set(Integer.parseInt(props.getProperty("crystals", "0")));
        } catch (NumberFormatException e) {
            crystals.set(0);
        }

        clearedStages.clear();
        String cleared = props.getProperty("clearedStages", "");
        if (!cleared.isBlank()) {
            for (String stageId : cleared.split("\\Q" + CLEARED_STAGES_DELIMITER + "\\E")) {
                if (!stageId.isBlank()) {
                    clearedStages.add(stageId);
                }
            }
        }
    }

    /** Persists current progress to disk. Silently no-ops on failure (e.g. read-only filesystem). */
    private static void save() {
        Properties props = new Properties();
        props.setProperty("crystals", String.valueOf(crystals.get()));
        props.setProperty("clearedStages", String.join(CLEARED_STAGES_DELIMITER, clearedStages));

        try {
            Files.createDirectories(SAVE_DIR);
            try (OutputStream out = Files.newOutputStream(SAVE_FILE)) {
                props.store(out, "Arclights save data - auto-generated, do not edit by hand");
            }
        } catch (IOException e) {
            System.err.println("PlayerProgress: failed to save progress (" + e.getMessage() + ")");
        }
    }
}