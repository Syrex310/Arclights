package com.arclights.entity.operator;

import com.arclights.entity.operator.skill.OperatorSkill;
import com.arclights.entity.operator.skill.SkillActivationType;
import com.arclights.entity.operator.skill.SkillRecoveryType;

import javafx.geometry.Point2D;

public class Specialist extends Operator {
    public static final int DEPLOY_COST = 5;

    public Specialist(double gridX, double gridY) {
        super(gridX, gridY, 1201, 530, 1, AttackType.PHYSICAL, 55.8, 0, 0, true, DEPLOY_COST);

        setSkill(new OperatorSkill(
            "Execution Mode",
            SkillRecoveryType.OFFENSIVE,
            SkillActivationType.DURATION,
            1,
            1,
            0,
            1,
            20.0,
            null,
            true
        ));
        
        this.relativeRangeOffsets.add(new Point2D(0, 0));
        this.relativeRangeOffsets.add(new Point2D(1, 0));
    }
}