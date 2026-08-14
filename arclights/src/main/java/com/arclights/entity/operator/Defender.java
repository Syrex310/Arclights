package com.arclights.entity.operator;

import com.arclights.entity.operator.skill.OperatorSkill;
import com.arclights.entity.operator.skill.SkillActivationType;
import com.arclights.entity.operator.skill.SkillRecoveryType;

import javafx.geometry.Point2D;

public class Defender extends Operator {
    /** DP cost to deploy a Defender, accessible without instantiating one. */
    public static final int DEPLOY_COST = 5;

    public Defender(double gridX, double gridY) {
        super(gridX, gridY, 2000, 5, 3, AttackType.PHYSICAL, 60, 50, 100, true, DEPLOY_COST);

        setSkill(new OperatorSkill(
            "Turtle Shell",
            SkillRecoveryType.AUTO,
            SkillActivationType.DURATION,
            20,
            1.0,
            1.0,
            15.0
        ));
        
        this.relativeRangeOffsets.add(new Point2D(0, 0));
    }
}