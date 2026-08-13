package com.arclights.entity.operator;

import javafx.geometry.Point2D;
import com.arclights.entity.operator.skill.OperatorSkill;
import com.arclights.entity.operator.skill.SkillActivationType;
import com.arclights.entity.operator.skill.SkillRecoveryType;

public class Defender extends Operator {
    public Defender(double gridX, double gridY) {
        super(gridX, gridY, 2000, 5, 3, AttackType.PHYSICAL, 60, 50, 100, true);

        setSkill(new OperatorSkill(
            "Heavy Strike",
            SkillRecoveryType.OFFENSIVE,
            SkillActivationType.NEXT_ATTACK,
            3,       // 3 SP
            1.0,
            2.0,     // next attack deals 2x damage
            0.0
        ));
        
        this.relativeRangeOffsets.add(new Point2D(0, 0));
    }
}