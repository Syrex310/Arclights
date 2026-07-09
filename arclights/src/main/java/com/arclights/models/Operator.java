package com.arclights.models;

public class Operator extends GameEntity {
    protected int range; // Tile attack radius
    protected int attackCooldown;
    private int cooldownTimer;

    public Operator(int gridX, int gridY, int hp, int atk, int range, int attackCooldown) {
        // Standardize grid tile positions to screen pixel coordinates
        super(gridX * 62 + 50, gridY * 62 + 50, hp, atk); 
        this.range = range;
        this.attackCooldown = attackCooldown;
        this.cooldownTimer = 0;
    }

    @Override
    public void update() {
        if (!isAlive) return;

        if (cooldownTimer > 0) {
            cooldownTimer--;
        } else {
            // Ready to attack! 
            // Later: scan map for active enemies, call target.takeDamage(this.atk)
            cooldownTimer = attackCooldown; // Reset timer
        }
    }
}