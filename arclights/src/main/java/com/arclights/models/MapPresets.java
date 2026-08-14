package com.arclights.models;

import com.arclights.models.MapPresets.StageData;

public class MapPresets {

    public static class StageData {
        private final String name;
        private final char[][] layout;
        private final MapConfig config;

        public StageData(String name, char[][] layout, MapConfig config) {
            this.name = name;
            this.layout = layout;
            this.config = config;
        }

        public String getName() { return name; }
        public char[][] getLayout() { return layout; }
        public MapConfig getConfig() { return config; }
    }

    public static final char[][] LEVEL_1_LAYOUT = {
        {'D', 'H', 'H', 'H', 'H', 'H', 'H', 'H'},
        {'S', 'M', 'H', 'M', 'M', 'M', 'M', 'M'},
        {'D', 'M', 'M', 'M', 'H', 'M', 'M', 'O'},
        {'D', 'H', 'H', 'H', 'H', 'H', 'H', 'H'},
        {'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D'}
    };
    public static final StageData STAGE_1_1 = new StageData(
        "Starter",
        LEVEL_1_LAYOUT,
        new MapConfig("/com/arclights/map1-0(1).png", 81, 81, 270, 150, 0, 0)
    );

    //skip
    public static final char[][] LEVEL_2_LAYOUT = {
        {'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H'},
        {'S', 'M', 'M', 'M', 'M', 'M', 'M', 'O'},
        {'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H'}
    };
    public static final StageData STAGE_1_2 = new StageData(
        "1-2 Straight Line",
        LEVEL_2_LAYOUT,
        new MapConfig("/com/arclights/map1-2.png", 0, 50.0, 2.0, 0) // 0 = Auto center horizontal
    );

    public static final char[][] LEVEL_3_LAYOUT = {
        {'D', 'D', 'D', 'D', 'S', 'D', 'D', 'D', 'D'},
        {'D', 'D', 'D', 'H', 'M', 'H', 'H', 'D', 'D'},
        {'D', 'D', 'H', 'H', 'M', 'H', 'H', 'D', 'D'},
        {'S', 'M', 'M', 'M', 'M', 'M', 'M', 'M', 'O'},
        {'D', 'D', 'D', 'H', 'H', 'M', 'H', 'H', 'D'},
        {'D', 'D', 'D', 'H', 'H', 'M', 'H', 'D', 'D'},
        {'D', 'D', 'D', 'D', 'D', 'O', 'D', 'D', 'D'}
    };
    public static final StageData STAGE_1_3 = new StageData(
        "Crossroad",
        LEVEL_3_LAYOUT,
        new MapConfig("/com/arclights/map1-1(8).png", 93.0, 91.3, 193.0, 5.5, 1.0, 0.0)
    );

    public static final char[][] LEVEL_4_LAYOUT = {
        {'D', 'S', 'D', 'D', 'D', 'D', 'D', 'D', 'D'},
        {'D', 'M', 'M', 'M', 'M', 'M', 'M', 'M', 'O'},
        {'D', 'H', 'H', 'D', 'D', 'H', 'D', 'D', 'H'},
        {'D', 'D', 'D', 'D', 'D', 'H', 'H', 'H', 'H'},
        {'S', 'M', 'M', 'M', 'M', 'M', 'M', 'M', 'O'},
        {'S', 'M', 'M', 'M', 'M', 'M', 'H', 'M', 'H'},
        {'D', 'H', 'H', 'H', 'H', 'M', 'H', 'M', 'H'},
        {'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D', 'D'}
    };
    public static final StageData STAGE_1_4 = new StageData(
        "Infinite",
        LEVEL_4_LAYOUT,
        new MapConfig("/com/arclights/map-inf(1).png", 82, 81, 227, 0, 1, 0)
    );

    public static MapConfig getConfigForLayout(char[][] layout) {
        if (layout == LEVEL_1_LAYOUT) return STAGE_1_1.getConfig();
        if (layout == LEVEL_2_LAYOUT) return STAGE_1_2.getConfig();
        if (layout == LEVEL_3_LAYOUT) return STAGE_1_3.getConfig();
        if (layout == LEVEL_4_LAYOUT) return STAGE_1_4.getConfig();
        return MapConfig.defaultConfig("/com/arclights/map1-1(8).png");
    }
}