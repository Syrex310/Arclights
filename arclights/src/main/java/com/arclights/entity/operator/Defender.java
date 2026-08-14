package com.arclights.entity.operator;

import com.arclights.entity.operator.skill.OperatorSkill;
import com.arclights.entity.operator.skill.SkillActivationType;
import com.arclights.entity.operator.skill.SkillRecoveryType;

import javafx.geometry.Point2D;

public class Defender extends Operator {
    /** DP cost to deploy a Defender, accessible without instantiating one. */
    public static final int DEPLOY_COST = 21;

    public Defender(double gridX, double gridY) {
        super(gridX, gridY, 3105, 365, 3, AttackType.PHYSICAL, 72, 0, 690, true, DEPLOY_COST);

        setSkill(new OperatorSkill(
            "DEF up",
            SkillRecoveryType.AUTO,
            SkillActivationType.DURATION,
            35,
            1.0,
            1.0,
            35 // def + 80%
        ));
        
        this.relativeRangeOffsets.add(new Point2D(0, 0));
    }
}