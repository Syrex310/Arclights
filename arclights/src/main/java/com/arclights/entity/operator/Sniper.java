package com.arclights.entity.operator;

import com.arclights.entity.operator.skill.OperatorSkill;
import com.arclights.entity.operator.skill.SkillActivationType;
import com.arclights.entity.operator.skill.SkillRecoveryType;

import javafx.geometry.Point2D;

public class Sniper extends Operator {
    /** DP cost to deploy a Sniper, accessible without instantiating one. */
    public static final int DEPLOY_COST = 13;

    public Sniper(double gridX, double gridY) {
        super(gridX, gridY, 1230, 535, 1, AttackType.PHYSICAL, 60, 0, 130, false, DEPLOY_COST);

        setSkill(new OperatorSkill(
            "Precision Burst",
            SkillRecoveryType.OFFENSIVE,
            SkillActivationType.NEXT_ATTACK,
            2,
            0.35,
            0,
            1,
            0,
            null,
            true
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
        this.relativeRangeOffsets.add(new Point2D(3, -1)); // Row above, 3 out
        this.relativeRangeOffsets.add(new Point2D(3, 0));  // Row center, 3 out
        this.relativeRangeOffsets.add(new Point2D(3, 1));  // Row below, 3 out
    }
}