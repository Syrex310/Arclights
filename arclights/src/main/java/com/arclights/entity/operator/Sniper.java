package com.arclights.entity.operator;

import javafx.geometry.Point2D;
import com.arclights.entity.operator.skill.OperatorSkill;
import com.arclights.entity.operator.skill.SkillActivationType;
import com.arclights.entity.operator.skill.SkillRecoveryType;

public class Sniper extends Operator {
    public Sniper(double gridX, double gridY) {
        super(gridX, gridY, 800, 70, 1, AttackType.PHYSICAL, 45, 0, 20, false);

        setSkill(new OperatorSkill(
            "Precision Burst",
            SkillRecoveryType.AUTO,
            SkillActivationType.DURATION,
            20,      // 20 SP
            1.20,    // +20% ATK while active
            1.0,
            20.0     // 20 seconds
        ));
        

        this.relativeRangeOffsets.add(new Point2D(0, 0));  // Own tile
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