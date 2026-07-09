package com.arclights.models;

public abstract class GameEntity {
    protected double x, y; // Smooth coordinates for rendering
    public int hp;
    protected int maxHp;
    protected int atk;
    protected boolean isAlive;

    public GameEntity(double x, double y, int hp, int atk) {
        this.x = x;
        this.y = y;
        this.hp = hp;
        this.maxHp = hp;
        this.atk = atk;
        this.isAlive = true;
    }

    // This is the "Minecraft Tick" method. Every entity defines what it does per frame.
    public abstract void update();

    public void takeDamage(int damage) {
        this.hp -= damage;
        if (this.hp <= 0) {
            this.hp = 0;
            this.isAlive = false;
        }
    }

    // Getters and Setters
    public double getX() { return x; }
    public double getY() { return y; }
    public boolean isAlive() { return isAlive; }
}