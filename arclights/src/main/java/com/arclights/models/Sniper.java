package com.arclights.models;

public class Sniper extends Operator {
    
    // Grid coordinates instead of pixel coordinates
    public int gridX, gridY;

    public Sniper(int gridX, int gridY) {
        // HP=500, ATK=25, Range=3 tiles, Cooldown=60 frames (~1 attack per second)
        super(gridX, gridY, 500, 25, 3, 60);
        this.gridX = gridX;
        this.gridY = gridY;
    }

    @Override
    public void update() {
        super.update(); // Handles attack cooldown timers automatically
    }
}