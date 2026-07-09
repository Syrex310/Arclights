package com.arclights.models;

public class Tile {
    // 0 = Ground, 1 = High Ground, 2 = Enemy Path
    private int type;
    private boolean isOccupied;

    public Tile(int type) {
        this.type = type;
        this.isOccupied = false;
    }

    public int getType() { return type; }
    
    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) { this.isOccupied = occupied; }

    // Helper methods to make rule validation incredibly clean later
    public boolean canPlaceMelee() { return type == 0 && !isOccupied; }
    public boolean canPlaceRanged() { return type == 1 && !isOccupied; }
    public boolean isEnemyPath() { return type == 2; }
}