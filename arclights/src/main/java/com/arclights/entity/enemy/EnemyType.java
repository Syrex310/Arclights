package com.arclights.entity.enemy;

import com.arclights.entity.GameEntity;

import javafx.scene.paint.Color;

public enum EnemyType {
    ORIGINIUM_SLUG(550, 130, 1, GameEntity.AttackType.PHYSICAL, 102, 0, true, 0,  1.0, Color.RED, 12),
    SOLDIER(2750, 300, 1, GameEntity.AttackType.PHYSICAL, 120, 0, true, 130, 1.1, Color.BLUE, 18),
    HOUND(820, 190, 1, GameEntity.AttackType.PHYSICAL, 84, 20, true, 0, 1.9, Color.ORANGE, 15),
    BIG_BOB(22000, 1900, 1, GameEntity.AttackType.PHYSICAL, 360, 30, true, 800, 0.5, Color.PURPLE, 25);

    private final double hp;
    private final double atk;
    private final int blockCount;
    private final GameEntity.AttackType attackType;
    private final double attackInterval;
    private final double resistance;
    private final boolean isGround;
    private final double defense;
    private final double speed;
    private final Color color;
    private final double radius;

    EnemyType(double hp, double atk, double speed, Color color, double radius) {
        this(hp, atk, 1, GameEntity.AttackType.PHYSICAL, 1.0, 0.0, true, 0.0, speed, color, radius);
    }

    EnemyType(
        double hp, double atk, int blockCount, GameEntity.AttackType attackType, 
        double attackInterval, double resistance, boolean isGround, double defense, 
        double speed, Color color, double radius) 
    {
        this.hp = hp;
        this.atk = atk;
        this.blockCount = blockCount;
        this.attackType = attackType;
        this.attackInterval = attackInterval;
        this.resistance = resistance;
        this.isGround = isGround;
        this.defense = defense;
        this.speed = speed;
        this.color = color;
        this.radius = radius;
    }

    public double getHp() { return hp; }
    public double getAtk() { return atk; }
    public int getBlockCount() { return blockCount; }
    public GameEntity.AttackType getAttackType() { return attackType; }
    public double getAttackInterval() { return attackInterval; }
    public double getResistance() { return resistance; }
    public boolean isGround() { return isGround; }
    public double getDefense() { return defense; }
    public double getSpeed() { return speed; }
    public Color getColor() { return color; }
    public double getRadius() { return radius; }
}