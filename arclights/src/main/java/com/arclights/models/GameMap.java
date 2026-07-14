package com.arclights.models;

public class GameMap {
    private final Tile[][] grid;
    private final int rows;
    private final int cols;

    public GameMap() {
        char[][] layout = {
            {'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H'},
            {'S', 'G', 'G', 'G', 'M', 'M', 'M', 'H'},
            {'H', 'H', 'H', 'G', 'M', 'H', 'M', 'H'},
            {'H', 'H', 'H', 'G', 'G', 'G', 'G', 'O'},
            {'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H'},
            {'H', 'H', 'H', 'H', 'H', 'H', 'H', 'H'}
        };

        this.rows = layout.length;
        this.cols = layout[0].length;
        this.grid = new Tile[rows][cols];

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = createTileFromChar(layout[r][c]);
            }
        }
    }

    private Tile createTileFromChar(char type) {
        // Classic switch statements safely supported by Java 11
        switch (type) {
            case 'H':
                return new Tile(Tile.TileType.RANGED_HIGH_GROUND, Tile.DeploymentType.RANGED_ONLY);
            case 'M':
                return new Tile(Tile.TileType.MELEE_GROUND, Tile.DeploymentType.MELEE_ONLY);
            case 'S':
                return new Tile(Tile.TileType.ENEMY_SPAWN, Tile.DeploymentType.NONE);
            case 'O':
                return new Tile(Tile.TileType.PLAYER_OBJECTIVE, Tile.DeploymentType.NONE);
            default:
                return new Tile(Tile.TileType.MELEE_GROUND, Tile.DeploymentType.NONE); // 'G' 
        }
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public Tile getTile(int row, int col) { return grid[row][col]; }
}