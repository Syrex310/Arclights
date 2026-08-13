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
    private boolean attackTriggered;
    private final List<Enemy> blockedEnemies = new ArrayList<>();
    protected List<Point2D> relativeRangeOffsets = new ArrayList<>(); // Relative (col, row) offsets

    // Constructor now receives true pixel positions directly from DeploymentManager
    public Operator(int gridX, int gridY, double pixelX, double pixelY, double hp, double atk, 
                    int blockCount, AttackType attackType, double attackInterval, 
                    double resistance, double defense, boolean isGround) {
        super(
            pixelX, 
            pixelY, 
            hp, atk, blockCount, attackType, attackInterval, resistance, defense, true, isGround
        );
        this.gridX = gridX;
        this.gridY = gridY;
        this.attackCooldownTimer = 0;
        this.facing = Direction.EAST; // Default facing configuration
    }

    // Overloaded constructor for fallback / default pixel calculation if needed
    public Operator(double gridX, double gridY, double hp, double atk, int blockCount, 
                    AttackType attackType, double attackInterval, double resistance, 
                    double defense, boolean isGround) {
        this((int) gridX, (int) gridY, gridX * 64 + 32, gridY * 64 + 32, hp, atk, blockCount, attackType, attackInterval, resistance, defense, isGround);
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

            // Correct 2D rotation matrix relative to EAST (0 degrees)
            switch (facing) {
                case NORTH:
                    rotatedX = dy;
                    rotatedY = -dx;
                    break;
                case SOUTH:
                    rotatedX = -dy;
                    rotatedY = dx;
                    break;
                case WEST:
                    rotatedX = -dx;
                    rotatedY = -dy;
                    break;
                case EAST:
                default:
                    rotatedX = dx;
                    rotatedY = dy;
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
                enemy.setBlockedBy(null);
            }
            blockedEnemies.clear();
            return;
        }

        blockedEnemies.removeIf(enemy -> !enemy.isAlive());

        // Melee unit blocking logic based on entity center distance
        if (isGround()) {
            for (Enemy enemy : activeEnemies) {
                if (!enemy.isAlive() || enemy.isBlocked()) continue;

                // Check if enemy is on the same tile as the Defender
                boolean isSameTile = (enemy.getCurrentGridX() == this.gridX) 
                                && (enemy.getCurrentGridY() == this.gridY);

                if (isSameTile) {
                    if (getRemainingBlockCount() >= enemy.getBlockCount()) {
                        blockedEnemies.add(enemy);
                        enemy.setBlocked(true);
                        enemy.setBlockedBy(this);
                        System.out.println("Enemy blocked at grid (" + gridX + ", " + gridY + ")! Remaining Block: " + getRemainingBlockCount());
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
                attackTriggered = true;
                System.out.println("Operator attacked enemy! Enemy HP: " + target.getHp()); 
                attackCooldownTimer = (int) getAttackInterval(); 
            }
        }
    }


    /** Returns true once when this operator performs an attack. */
    public boolean consumeAttackTriggered() {
        boolean triggered = attackTriggered;
        attackTriggered = false;
        return triggered;
    }

    private Enemy findTargetInGridRange(List<Enemy> activeEnemies) {
        List<Point2D> targetTiles = getAbsoluteRangeTiles();

        for (Enemy enemy : activeEnemies) {
            if (!enemy.isAlive()) continue;

            for (Point2D tile : targetTiles) {
                if ((int) tile.getX() == enemy.getCurrentGridX() && (int) tile.getY() == enemy.getCurrentGridY()) {
                    return enemy;
                }
            }
        }
        return null;
    }

    @Override
    public void update() {}
}