package com.arclights.models;

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
        {'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H'},
        {'S', 'M', 'M', 'M', 'M', 'M', 'M', 'H'},
        {'H', 'H', 'H', 'M', 'M', 'H', 'M', 'H'},
        {'H', 'H', 'H', 'M', 'M', 'M', 'M', 'O'},
        {'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H'},
        {'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H'}
    };
    public static final StageData STAGE_1_1 = new StageData(
        "1-1 Main Corridor",
        LEVEL_1_LAYOUT,
        new MapConfig("/com/arclights/map1-1.png", 64.0, 120.0, 80.0, 2.0)
    );

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
        "1-3 Crossroads",
        LEVEL_3_LAYOUT,
        new MapConfig("/com/arclights/map1-1(8).png", 93.0, 91.3, 193.0, 5.5, 1.0, 0.0)
    );

    public static MapConfig getConfigForLayout(char[][] layout) {
        if (layout == LEVEL_1_LAYOUT) return STAGE_1_1.getConfig();
        if (layout == LEVEL_2_LAYOUT) return STAGE_1_2.getConfig();
        if (layout == LEVEL_3_LAYOUT) return STAGE_1_3.getConfig();
        return MapConfig.defaultConfig("/com/arclights/map1-1(8).png");
    }
}