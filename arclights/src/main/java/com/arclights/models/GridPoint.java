package com.arclights.models;

import java.util.Objects;

/**
 * Simple (row, col) coordinate on the map grid.
 * Shared between EnemyManager's pathfinding and wave/spawn configuration
 * so custom enemy routes can be described in grid terms instead of pixels.
 */
public class GridPoint {
    public final int r;
    public final int c;

    public GridPoint(int r, int c) {
        this.r = r;
        this.c = c;
    }

    public int getRow() { return r; }
    public int getCol() { return c; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GridPoint)) return false;
        GridPoint that = (GridPoint) o;
        return r == that.r && c == that.c;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, c);
    }

    @Override
    public String toString() {
        return "(" + r + "," + c + ")";
    }
}
