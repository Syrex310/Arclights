package com.arclights.entity.operator;

import javafx.geometry.Point2D;

public class Defender extends Operator {
    public Defender(double gridX, double gridY) {
        // Super arguments: X, Y, HP, ATK, Block, AtkType, Interval, Res, Def, Ground
        super(gridX, gridY, 2000, 5, 3, AttackType.PHYSICAL, 60, 50, 100, true);
        
        // Arknights Defender Range: Just its own tile
        this.relativeRangeOffsets.add(new Point2D(0, 0));
    }
}