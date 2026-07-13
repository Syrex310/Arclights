package com.arclights.models;

import java.util.ArrayList;
import java.util.List;

public class Operator extends GameEntity {
    protected double range;
    private int attackCooldownTimer;
    private final List<Enemy> blockedEnemies = new ArrayList<>();

    public Operator(double gridX, double gridY, double hp, double atk, double range, double attackInterval) {
        // Compute center pixel coordinate of the designated tile
        super(
            gridX * (60 + 2) + 50 + 30,
            gridY * (60 + 2) + 50 + 30,
            hp,
            atk,
            1,
            AttackType.PHYSICAL,
            attackInterval,
            0.0,
            0.0,
            true,
            true                        
        ); 
        this.range = range;
        this.attackCooldownTimer = 0;
    }

    public Operator(double gridX, double gridY, double hp, double atk, int blockCount, 
                    AttackType attackType, double attackInterval, double resistance, 
                    double defense, double range, boolean isGround) {
        super(
            gridX * (60 + 2) + 50 + 30, 
            gridY * (60 + 2) + 50 + 30, 
            hp, atk, blockCount, attackType, attackInterval, resistance, defense, true, isGround
        );
        this.range = range;
        this.attackCooldownTimer = 0;
    }

    public int getRemainingBlockCount() {
        int usedBlock = 0;
        for (Enemy enemy : blockedEnemies) {
            usedBlock += enemy.getBlockCount(); // Deduct the enemy's specific block cost
        }
        return Math.max(0, getBlockCount() - usedBlock);
    }

    // Pass the list of enemies into loop frame update
    public void update(List<Enemy> activeEnemies) {
        // Rule: If the operator dies, release all currently blocked enemies so they can move again
        if (!isAlive()) {
            for (Enemy enemy : blockedEnemies) {
                enemy.setBlocked(false);
            }
            blockedEnemies.clear();
            return;
        }

        // Clean up any enemies that died during the last frame to free up block slots
        blockedEnemies.removeIf(enemy -> !enemy.isAlive());

        // Handle Blocking Detection
        if (isGround()) {
            for (Enemy enemy : activeEnemies) {
                // Skip dead enemies or ones already blocked by another operator
                if (!enemy.isAlive() || enemy.isBlocked()) continue;

                // Check distance to see if the enemy "meets" the operator (within 30 pixels / half tile)
                double dx = enemy.getX() - this.getX();
                double dy = enemy.getY() - this.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance <= 30.0) {
                    // Only block if remaining block capacity can fully absorb the enemy's block weight
                    if (getRemainingBlockCount() >= enemy.getBlockCount()) {
                        blockedEnemies.add(enemy);
                        enemy.setBlocked(true);
                        System.out.println("Enemy blocked! Remaining Operator Block Capacity: " + getRemainingBlockCount());
                    }
                    // If capacity is insufficient (e.g., remaining is 1, enemy requires 2), they pass right through
                }
            }
        }   


        if (attackCooldownTimer > 0) {
            attackCooldownTimer--;
        } else {
            // Tactical targeting: Prioritize attacking the enemies currently being blocked first.
            Enemy target = null;
            if (!blockedEnemies.isEmpty()) {
                target = blockedEnemies.get(0);
            } else {
                target = findTarget(activeEnemies); // Fallback to normal range scan if no one is blocked
            }

            if (target != null) {
                target.takeDamage(getAtk(), getAttackType()); 
                System.out.println("Operator attacked enemy! Enemy HP left: " + target.getHp()); 
                attackCooldownTimer = (int) getAttackInterval(); 
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
                return enemy;
            }
        }
        return null; // No enemies in range
    }

    @Override
    public void update() {
        // Fallback
    }

    // Getter and Setter for Operator
    public double getRange() { return range; }
    public void setRange(double range) { this.range = range; }
}