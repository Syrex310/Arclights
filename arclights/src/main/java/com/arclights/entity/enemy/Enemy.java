package com.arclights.entity.enemy;

import java.util.List;

import com.arclights.entity.GameEntity;
import com.arclights.entity.operator.Operator;

import javafx.geometry.Point2D;

public class Enemy extends GameEntity {
    private double speed;
    private double speedDefault;
    private List<Point2D> waypoints;
    private int currentWaypointIndex;
    private boolean isBlocked = false;

    // The operator currently blocking this enemy (null if not blocked)
    private Operator blockedByOperator;
    private int attackCooldownTimer = 0;
    private boolean attackTriggered = false;

    // Track current grid column & row for clean operator targeting
    private int currentGridX;
    private int currentGridY;

    public Enemy(double startX, double startY, double hp, double atk, double speed, List<Point2D> waypoints) {
        super(
            startX,
            startY,
            hp,
            atk,
            1,
            AttackType.PHYSICAL,
            60,
            0.0,
            0.0,
            true,
            true
        );
        this.speed = speed;
        this.waypoints = waypoints;
        this.currentWaypointIndex = 0;
        speedDefault = speed;
    }

    public Enemy(double startX, double startY, double hp, double atk, int blockCount, 
                 AttackType attackType, double attackInterval, double resistance, boolean isGround, 
                 double defense, double speed, List<Point2D> waypoints) {
        super(startX, startY, hp, atk, blockCount, attackType, attackInterval, resistance, defense, true, isGround);
        this.speed = speed;
        this.waypoints = waypoints;
        this.currentWaypointIndex = 0;
        speedDefault = speed;
    }

    @Override
    public void update() {
        if (!isAlive() || waypoints == null || currentWaypointIndex >= waypoints.size()) return;

        if (isBlocked()) {
            attackBlockingOperator();
            return;
        }

        // Get our current target checkpoint
        Point2D target = waypoints.get(currentWaypointIndex);

        // Calculate distance to target waypoint
        double dx = target.getX() - getX();
        double dy = target.getY() - getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // Snap to waypoint when close enough
        if (distance <= Math.max(speed, 2.0)) {
            setX(target.getX());
            setY(target.getY());
            currentWaypointIndex++;
        } else {
            // Move smoothly toward the target waypoint using normalized velocity
            setX(getX() + (dx / distance) * speed);
            setY(getY() + (dy / distance) * speed);
        }

    }

    /**
     * While blocked, the enemy attacks the operator holding it in place on
     * an interval mirroring Operator's own attack-cooldown pattern.
     */
    private void attackBlockingOperator() {
        if (blockedByOperator == null || !blockedByOperator.isAlive()) return;

        if (attackCooldownTimer > 0) {
            attackCooldownTimer--;
        } else {
            blockedByOperator.takeDamage(getAtk(), getAttackType());
            attackTriggered = true;
            System.out.println("Enemy attacked operator! Operator HP: " + blockedByOperator.getHp());
            attackCooldownTimer = (int) getAttackInterval();
        }
    }

    /** Returns true once when this enemy performs an attack on the blocking operator. */
    public boolean consumeAttackTriggered() {
        boolean triggered = attackTriggered;
        attackTriggered = false;
        return triggered;
    }

    /**
     * Helper to update current grid location from layout parameters (called by EnemyManager)
     */
    public void updateGridPosition(double offsetX, double offsetY, double tileWidth, double tileHeight, double paddingX, double paddingY) {
        this.currentGridX = (int) Math.floor((getX() - offsetX) / (tileWidth + paddingX));
        this.currentGridY = (int) Math.floor((getY() - offsetY) / (tileHeight + paddingY));
    }

    // Grid Location Getters for Operator Range Checks
    public int getCurrentGridX() { return currentGridX; }
    public int getCurrentGridY() { return currentGridY; }

    // Getters and Setters for Enemy
    public boolean isBlocked() { return isBlocked; }
    public void setBlocked(boolean blocked) { this.isBlocked = blocked; }

    public Operator getBlockedBy() { return blockedByOperator; }
    public void setBlockedBy(Operator operator) {
        this.blockedByOperator = operator;
        // Reset the cooldown so a newly-blocking operator isn't hit instantly
        // by leftover cooldown state from a previous blocker.
        if (operator == null) {
            attackCooldownTimer = 0;
        }
    }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }
    public double getDefaultSpeed() { return speedDefault; }

    public List<Point2D> getWaypoints() { return waypoints; }
    public void setWaypoints(List<Point2D> waypoints) { this.waypoints = waypoints; }

    public int getCurrentWaypointIndex() { return currentWaypointIndex; }
    public void setCurrentWaypointIndex(int index) { this.currentWaypointIndex = index; }
}