package com.arclights.entity.operator;

import com.arclights.entity.operator.skill.OperatorSkill;
import com.arclights.entity.operator.skill.SkillActivationType;
import com.arclights.entity.operator.skill.SkillRecoveryType;

import javafx.geometry.Point2D;

public class Vanguard extends Operator {
    public static final int DEPLOY_COST = 10;

    public Vanguard(double gridX, double gridY) {
        super(gridX, gridY, 2226, 570, 2, AttackType.PHYSICAL, 60, 0, 360, true, DEPLOY_COST);

        setSkill(new OperatorSkill(
            "Impaler Lance",
            SkillRecoveryType.OFFENSIVE,
            SkillActivationType.NEXT_ATTACK,
            4,
            1,
            0,
            0.8,
            0.0,
            null,
            true // give dp when activated
        ));
        
        this.relativeRangeOffsets.add(new Point2D(0, 0));
        this.relativeRangeOffsets.add(new Point2D(1, 0));
    }
}