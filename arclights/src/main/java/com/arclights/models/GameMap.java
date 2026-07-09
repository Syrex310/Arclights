package com.arclights.models;

public class GameMap {
    private Tile[][] grid;
    private int rows;
    private int cols;

    public GameMap() {
        // Our blueprint layout array
        int[][] layout = {
            {1, 1, 1, 1, 1, 1, 1, 1},
            {2, 2, 2, 2, 0, 0, 0, 1},
            {1, 1, 1, 2, 0, 1, 0, 1},
            {1, 1, 1, 2, 2, 2, 2, 2},
            {1, 1, 1, 1, 1, 1, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1}
        };

        this.rows = layout.length;
        this.cols = layout[0].length;
        this.grid = new Tile[rows][cols];

        // Convert the raw integers into strong Tile objects
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                grid[r][c] = new Tile(layout[r][c]);
            }
        }
    }

    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public Tile getTile(int row, int col) { return grid[row][col]; }
}