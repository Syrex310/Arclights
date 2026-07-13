package com.arclights.models;

import java.util.List;

import javafx.geometry.Point2D;

public class Enemy extends GameEntity {
    private double speed;
    private List<Point2D> waypoints;
    private int currentWaypointIndex;
    private boolean isBlocked = false;

    public Enemy(double startX, double startY, double hp, double atk, double speed, List<Point2D> waypoints) {
        super(
            startX,
            startY,
            hp,
            atk,
            1,
            AttackType.PHYSICAL,
            1.5,
            0.0,
            0.0,
            true,
            true
        );
        this.speed = speed;
        this.waypoints = waypoints;
        this.currentWaypointIndex = 0;
    }

    public Enemy(double startX, double startY, double hp, double atk, int blockCount, 
                 AttackType attackType, double attackInterval, double resistance, boolean isGround, 
                 double defense, double speed, List<Point2D> waypoints) {
        super(startX, startY, hp, atk, blockCount, attackType, attackInterval, resistance, defense, true, isGround);
        this.speed = speed;
        this.waypoints = waypoints;
        this.currentWaypointIndex = 0;
    }

    @Override
    public void update() {
        if (!isAlive() || waypoints == null || currentWaypointIndex >= waypoints.size()) return;

        if (isBlocked()) return;
        // Get our current target checkpoint
        Point2D target = waypoints.get(currentWaypointIndex);

        // Calculate distance to target
        double dx = target.getX() - getX();
        double dy = target.getY() - getY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        // If we are close enough to the checkpoint, switch to the next one
        if (distance <= speed) {
            setX(target.getX());
            setY(target.getY());
            currentWaypointIndex++;
        } else {
            // Move smoothly toward the target waypoint using a normalized velocity vector
            setX(getX() + (dx / distance) * speed);
            setY(getY() + (dy / distance) * speed);
        }
    }

    // Getters and Setters for Enemy
    public boolean isBlocked() { return isBlocked; }
    public void setBlocked(boolean blocked) { this.isBlocked = blocked; }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }

    public List<Point2D> getWaypoints() { return waypoints; }
    public void setWaypoints(List<Point2D> waypoints) { this.waypoints = waypoints; }

    public int getCurrentWaypointIndex() { return currentWaypointIndex; }
    public void setCurrentWaypointIndex(int index) { this.currentWaypointIndex = index; }
}