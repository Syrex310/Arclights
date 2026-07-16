package com.arclights.entity.enemy;

import javafx.scene.paint.Color;

public enum EnemyType {
    ORIGINIUM_SLUG(1000, 10, 1.0, Color.RED, 15),
    SOLDIER(1800, 30, 0.7, Color.BLUE, 18),
    HOUND(600, 20, 2.2, Color.ORANGE, 12),
    BOSS(5000, 100, 0.4, Color.PURPLE, 25);

    private final double hp;
    private final double atk;
    private final double speed;
    private final Color color;
    private final double radius;

    EnemyType(double hp, double atk, double speed, Color color, double radius) {
        this.hp = hp;
        this.atk = atk;
        this.speed = speed;
        this.color = color;
        this.radius = radius;
    }

    public double getHp() { return hp; }
    public double getAtk() { return atk; }
    public double getSpeed() { return speed; }
    public Color getColor() { return color; }
    public double getRadius() { return radius; }
}