package com.arclights.entity.operator;

import com.arclights.entity.operator.skill.OperatorSkill;
import com.arclights.entity.operator.skill.SkillActivationType;
import com.arclights.entity.operator.skill.SkillRecoveryType;

import javafx.geometry.Point2D;

public class Supporter extends Operator {
    public static final int DEPLOY_COST = 8;

    public Supporter(double gridX, double gridY) {
        super(gridX, gridY, 1603, 368, 1, AttackType.ARTS, 78, 0, 233, false, DEPLOY_COST);

        setSkill(new OperatorSkill(
            "Wish of Burial Beyond the Light",
            SkillRecoveryType.AUTO,
            SkillActivationType.DURATION,
            60,
            0,
            0,
            0,
            1e9,
            null,
            true
        ));
        
        this.relativeRangeOffsets.add(new Point2D(0, -2)); 
        this.relativeRangeOffsets.add(new Point2D(0, -1)); 
        this.relativeRangeOffsets.add(new Point2D(0, 0)); 
        this.relativeRangeOffsets.add(new Point2D(0, 1));
        this.relativeRangeOffsets.add(new Point2D(0, 2)); 

        this.relativeRangeOffsets.add(new Point2D(1, -1)); 
        this.relativeRangeOffsets.add(new Point2D(1, 0));  
        this.relativeRangeOffsets.add(new Point2D(1, 1));

        this.relativeRangeOffsets.add(new Point2D(-1, -1)); 
        this.relativeRangeOffsets.add(new Point2D(-1, 0));
        this.relativeRangeOffsets.add(new Point2D(-1, 1));

        this.relativeRangeOffsets.add(new Point2D(-2, 0));
        this.relativeRangeOffsets.add(new Point2D(2, 0));
    }
}