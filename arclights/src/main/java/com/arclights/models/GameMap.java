package com.arclights.models;

public class GameMap {
    private final Tile[][] grid;
    private final int rows;
    private final int cols;
    private MapConfig mapConfig;

    public GameMap(char[][] layout) {
        this(layout, MapConfig.defaultConfig("/com/arclights/map1-1(8).png"));
    }
    
    public GameMap(char[][] layout, MapConfig mapConfig) {
        if (layout == null || layout.length == 0) {
            throw new IllegalArgumentException("Map layout cannot be empty.");
        }
        this.rows = layout.length;
        this.cols = layout[0].length;
        this.grid = new Tile[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = createTileFromChar(layout[r][c]);
            }
        }
        this.mapConfig = mapConfig;
    }

    public MapConfig getMapConfig() {
        return mapConfig != null ? mapConfig : MapConfig.defaultConfig("/com/arclights/map1-1(8).png");
    }

    private Tile createTileFromChar(char type) {
        switch (type) {
            case 'H':
                return new Tile(Tile.TileType.RANGED_HIGH_GROUND, Tile.DeploymentType.RANGED_ONLY);
            case 'M':
                return new Tile(Tile.TileType.MELEE_GROUND, Tile.DeploymentType.MELEE_ONLY);
            case 'S':
                return new Tile(Tile.TileType.ENEMY_SPAWN, Tile.DeploymentType.NONE);
            case 'O':
                return new Tile(Tile.TileType.PLAYER_OBJECTIVE, Tile.DeploymentType.NONE);
            case 'D':
                return new Tile(Tile.TileType.DECORATION, Tile.DeploymentType.NONE);
            default:
                return new Tile(Tile.TileType.DECORATION, Tile.DeploymentType.NONE);
        }
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public Tile getTile(int row, int col) { return grid[row][col]; }
}