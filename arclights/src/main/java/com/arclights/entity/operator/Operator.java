package com.arclights.entity.operator;

import java.util.ArrayList;
import java.util.List;

import com.arclights.entity.GameEntity;
import com.arclights.entity.enemy.Enemy;

import javafx.geometry.Point2D;

public class Operator extends GameEntity {
    public enum Direction {
        NORTH, EAST, SOUTH, WEST
    }

    private final int gridX;
    private final int gridY;
    private Direction facing;
    private int attackCooldownTimer;
    private final List<Enemy> blockedEnemies = new ArrayList<>();
    protected List<Point2D> relativeRangeOffsets = new ArrayList<>(); // Collection of relative (col, row) offsets

    public Operator(double gridX, double gridY, double hp, double atk, int blockCount, 
                    AttackType attackType, double attackInterval, double resistance, 
                    double defense, boolean isGround) {
        super(
            gridX * (62) + 50 + 30, 
            gridY * (62) + 50 + 30, 
            hp, atk, blockCount, attackType, attackInterval, resistance, defense, true, isGround
        );
        this.gridX = (int) gridX;
        this.gridY = (int) gridY;
        this.attackCooldownTimer = 0;
        this.facing = Direction.EAST; // Default facing configuration
    }

    public void setFacing(Direction facing) {
        this.facing = facing;
    }

    public Direction getFacing() {
        return facing;
    }

    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }


    public List<Point2D> getAbsoluteRangeTiles() {
        List<Point2D> absoluteTiles = new ArrayList<>();
        for (Point2D offset : relativeRangeOffsets) {
            double dx = offset.getX();
            double dy = offset.getY();
            double rotatedX = dx;
            double rotatedY = dy;

            // Rotate coordinates based on facing direction
            switch (facing) {
                case NORTH:
                    rotatedX = -dy;
                    rotatedY = -dx;
                    break;
                case SOUTH:
                    rotatedX = dy;
                    rotatedY = dx;
                    break;
                case WEST:
                    rotatedX = -dx;
                    rotatedY = -dy;
                    break;
                case EAST:
                default:
                    // Default layout blueprint is designed facing EAST
                    break;
            }

            absoluteTiles.add(new Point2D(gridX + rotatedX, gridY + rotatedY));
        }
        return absoluteTiles;
    }

    public int getRemainingBlockCount() {
        int usedBlock = 0;
        for (Enemy enemy : blockedEnemies) {
            usedBlock += enemy.getBlockCount();
        }
        return Math.max(0, getBlockCount() - usedBlock);
    }

    public void update(List<Enemy> activeEnemies) {
        if (!isAlive()) {
            for (Enemy enemy : blockedEnemies) {
                enemy.setBlocked(false);
            }
            blockedEnemies.clear();
            return;
        }

        blockedEnemies.removeIf(enemy -> !enemy.isAlive());

        // Melee units handle proximity blocking logic
        if (isGround()) {
            for (Enemy enemy : activeEnemies) {
                if (!enemy.isAlive() || enemy.isBlocked()) continue;

                double dx = enemy.getX() - this.getX();
                double dy = enemy.getY() - this.getY();
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance <= 30.0) {
                    if (getRemainingBlockCount() >= enemy.getBlockCount()) {
                        blockedEnemies.add(enemy);
                        enemy.setBlocked(true);
                        System.out.println("Enemy blocked! Remaining Block: " + getRemainingBlockCount());
                    }
                }
            }
        }   

        if (attackCooldownTimer > 0) {
            attackCooldownTimer--;
        } else {
            Enemy target = null;
            if (!blockedEnemies.isEmpty()) {
                target = blockedEnemies.get(0);
            } else {
                target = findTargetInGridRange(activeEnemies);
            }

            if (target != null) {
                target.takeDamage(getAtk(), getAttackType()); 
                System.out.println("Operator attacked enemy! Enemy HP: " + target.getHp()); 
                attackCooldownTimer = (int) getAttackInterval(); 
            }
        }
    }


    private Enemy findTargetInGridRange(List<Enemy> activeEnemies) {
        List<Point2D> targetTiles = getAbsoluteRangeTiles();

        for (Enemy enemy : activeEnemies) {
            if (!enemy.isAlive()) continue;

            // Reverse map the pixel position back to raw grid indexes
            int enemyGridX = (int) ((enemy.getX() - 50) / 62);
            int enemyGridY = (int) ((enemy.getY() - 50) / 62);

            for (Point2D tile : targetTiles) {
                if ((int) tile.getX() == enemyGridX && (int) tile.getY() == enemyGridY) {
                    return enemy;
                }
            }
        }
        return null;
    }

    @Override
    public void update() {}
}