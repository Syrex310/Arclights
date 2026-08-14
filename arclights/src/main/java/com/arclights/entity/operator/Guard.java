package com.arclights.entity.operator;

import com.arclights.entity.operator.skill.OperatorSkill;
import com.arclights.entity.operator.skill.SkillActivationType;
import com.arclights.entity.operator.skill.SkillRecoveryType;

import javafx.geometry.Point2D;

public class Guard extends Operator {
    public static final int DEPLOY_COST = 24;

    public Guard(double gridX, double gridY) {
        super(gridX, gridY, 2821, 765, 2, AttackType.PHYSICAL, 72, 0, 370, true, DEPLOY_COST);

        setSkill(new OperatorSkill(
            "Chainsaw Extension Module",
            SkillRecoveryType.AUTO,
            SkillActivationType.DURATION,
            30,
            1,
            0.35,
            0,
            30,
            null,
            true
        ));
        
        this.relativeRangeOffsets.add(new Point2D(0, 0));
        this.relativeRangeOffsets.add(new Point2D(1, 0));
    }
}