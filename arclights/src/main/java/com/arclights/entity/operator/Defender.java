package com.arclights.entity.operator;

import javafx.geometry.Point2D;

public class Defender extends Operator {
    public Defender(double gridX, double gridY) {
        super(gridX, gridY, 2000, 5, 3, AttackType.PHYSICAL, 60, 50, 100, true);
        
        this.relativeRangeOffsets.add(new Point2D(0, 0));
    }
}