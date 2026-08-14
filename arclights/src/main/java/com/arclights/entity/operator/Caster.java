package com.arclights.entity.operator;

import com.arclights.entity.operator.skill.OperatorSkill;
import com.arclights.entity.operator.skill.SkillActivationType;
import com.arclights.entity.operator.skill.SkillRecoveryType;

import javafx.geometry.Point2D;

public class Caster extends Operator {
    public static final int DEPLOY_COST = 21;

    public Caster(double gridX, double gridY) {
        super(gridX, gridY, 1743, 645, 1, AttackType.ARTS, 96, 20, 122, false, DEPLOY_COST);

        setSkill(new OperatorSkill(
            "Ignition",
            SkillRecoveryType.AUTO,
            SkillActivationType.NEXT_ATTACK,
            5,
            0.0,  
            0.0,  
            2.7,  
            0.0,
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
        this.relativeRangeOffsets.add(new Point2D(3, 0));  // Row center, 3 out
    }
}