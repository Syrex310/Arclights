package com.arclights.entity.operator;

import com.arclights.entity.operator.skill.OperatorSkill;
import com.arclights.entity.operator.skill.SkillActivationType;
import com.arclights.entity.operator.skill.SkillRecoveryType;

import javafx.geometry.Point2D;

public class Guard extends Operator {
    public static final int DEPLOY_COST = 5;

    public Guard(double gridX, double gridY) {
        super(gridX, gridY, 2000, 5, 3, AttackType.PHYSICAL, 60, 50, 100, true, DEPLOY_COST);

        setSkill(new OperatorSkill(
            "Heavy Strike",
            SkillRecoveryType.OFFENSIVE,
            SkillActivationType.NEXT_ATTACK,
            2,
            1.0,
            2.0,
            0.0
        ));
        
        this.relativeRangeOffsets.add(new Point2D(0, 0));
        this.relativeRangeOffsets.add(new Point2D(1, 0));
    }
}