package com.arclights.models;

import java.util.List;

public class Operator extends GameEntity {
    protected int range; // Tile attack radius (multiplied by tileSize for pixels)
    protected int attackCooldown;
    private int cooldownTimer;

    public Operator(int gridX, int gridY, int hp, int atk, int range, int attackCooldown) {
        // Compute center pixel coordinate of the designated tile
        super(gridX * (60 + 2) + 50 + 30, gridY * (60 + 2) + 50 + 30, hp, atk); 
        this.range = range;
        this.attackCooldown = attackCooldown;
        this.cooldownTimer = 0;
    }

    // Pass the list of enemies into our loop frame update
    public void update(List<Enemy> activeEnemies) {
        if (!isAlive) return;

        if (cooldownTimer > 0) {
            cooldownTimer--;
        } else {
            // Scan for the first enemy inside our attack radius range
            Enemy target = findTarget(activeEnemies);
            if (target != null) {
                target.takeDamage(this.atk);
                System.out.println("Sniper attacked enemy! Enemy HP left: " + target.hp);
                cooldownTimer = attackCooldown; // Reset the attack clock
            }
        }
    }

    private Enemy findTarget(List<Enemy> activeEnemies) {
        double maxPixelRange = this.range * 62; // Convert tile range to exact pixel radius

        for (Enemy enemy : activeEnemies) {
            if (!enemy.isAlive()) continue;

            // Simple Pythagorean distance check
            double dx = enemy.getX() - this.getX();
            double dy = enemy.getY() - this.getY();
            double distance = Math.sqrt(dx * dx + dy * dy);

            if (distance <= maxPixelRange) {
                return enemy; // Target acquired!
            }
        }
        return null; // No enemies in range
    }

    @Override
    public void update() {
        // Fallback method required by GameEntity base declaration
    }
}