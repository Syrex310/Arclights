package com.arclights.models;

import java.util.List;

import javafx.geometry.Point2D;

public class Enemy extends GameEntity {
    private double speed;
    private List<Point2D> waypoints;
    private int currentWaypointIndex;

    public Enemy(double startX, double startY, int hp, int atk, double speed, List<Point2D> waypoints) {
        super(startX, startY, hp, atk);
        this.speed = speed;
        this.waypoints = waypoints;
        this.currentWaypointIndex = 0;
    }

    @Override
    public void update() {
        if (!isAlive || waypoints == null || currentWaypointIndex >= waypoints.size()) return;

        // Get our current target checkpoint
        Point2D target = waypoints.get(currentWaypointIndex);

        // Calculate distance to target
        double dx = target.getX() - this.x;
        double dy = target.getY() - this.y;
        double distance = Math.sqrt(dx * dx + dy * dy);

        // If we are close enough to the checkpoint, switch to the next one
        if (distance <= speed) {
            this.x = target.getX();
            this.y = target.getY();
            currentWaypointIndex++;
        } else {
            // Move smoothly toward the target waypoint using a normalized velocity vector
            this.x += (dx / distance) * speed;
            this.y += (dy / distance) * speed;
        }
    }
}