package com.arclights.models;

public class Defender extends Operator{
    public double gridX, gridY;

    public Defender(double gridX, double gridY) {
        super(gridX, gridY, 2000, 1, 3, AttackType.PHYSICAL, 60, 50, 100, 1, true);
        this.gridX = gridX;
        this.gridY = gridY;
    }

    @Override
    public void update() {
        super.update();
    }
}
