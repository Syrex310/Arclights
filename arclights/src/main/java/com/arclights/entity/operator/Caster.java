package com.arclights.entity.operator;

import com.arclights.entity.operator.skill.OperatorSkill;
import com.arclights.entity.operator.skill.SkillActivationType;
import com.arclights.entity.operator.skill.SkillRecoveryType;

import javafx.geometry.Point2D;

public class Caster extends Operator {
    public static final int DEPLOY_COST = 5;

    public Caster(double gridX, double gridY) {
        super(gridX, gridY, 2000, 5, 3, AttackType.ARTS, 60, 50, 100, false, DEPLOY_COST);

        setSkill(new OperatorSkill(
            "Heavy Strike",
            SkillRecoveryType.AUTO,
            SkillActivationType.DURATION,
            25,
            1.5,
            1.0,
            20.0
        ));
        
        this.relativeRangeOffsets.add(new Point2D(0, -1));
        this.relativeRangeOffsets.add(new Point2D(0, 0));  // Own tile
        this.relativeRangeOffsets.add(new Point2D(0, 1)); 
        this.relativeRangeOffsets.add(new Point2D(1, -1)); // Row above, 1 out
        this.relativeRangeOffsets.add(new Point2D(1, 0));  // Row center, 1 out
        this.relativeRangeOffsets.add(new Point2D(1, 1));  // Row below, 1 out
        this.relativeRangeOffsets.add(new Point2D(2, -1)); // Row above, 2 out
        this.relativeRangeOffsets.add(new Point2D(2, 0));  // Row center, 2 out
        this.relativeRangeOffsets.add(new Point2D(2, 1));  // Row below, 2 out
        this.relativeRangeOffsets.add(new Point2D(3, 0));  // Row center, 3 out
    }
}