package com.arclights.entity.operator;

import com.arclights.entity.operator.skill.OperatorSkill;
import com.arclights.entity.operator.skill.SkillActivationType;
import com.arclights.entity.operator.skill.SkillRecoveryType;

import javafx.geometry.Point2D;

public class Supporter extends Operator {
    public static final int DEPLOY_COST = 5;

    public Supporter(double gridX, double gridY) {
        super(gridX, gridY, 2000, 5, 3, AttackType.ARTS, 60, 50, 100, false, DEPLOY_COST);

        setSkill(new OperatorSkill(
            "Heavy Strike",
            SkillRecoveryType.OFFENSIVE,
            SkillActivationType.NEXT_ATTACK,
            2,
            1.0,
            2.0,
            0.0
        ));
        
        this.relativeRangeOffsets.add(new Point2D(0, -1)); 
        this.relativeRangeOffsets.add(new Point2D(0, 0)); // Own tile
        this.relativeRangeOffsets.add(new Point2D(0, 1));
        this.relativeRangeOffsets.add(new Point2D(1, -1)); // Row above, 1 out
        this.relativeRangeOffsets.add(new Point2D(1, 0));  // Row center, 1 out
        this.relativeRangeOffsets.add(new Point2D(1, 1));  // Row below, 1 out
        this.relativeRangeOffsets.add(new Point2D(2, -1)); // Row above, 2 out
        this.relativeRangeOffsets.add(new Point2D(2, 0));  // Row center, 2 out
        this.relativeRangeOffsets.add(new Point2D(2, 1));  // Row below, 2 out
        this.relativeRangeOffsets.add(new Point2D(-1, -1)); // Row above, 1 in
        this.relativeRangeOffsets.add(new Point2D(-1, 0));  // Row center, 1 in
        this.relativeRangeOffsets.add(new Point2D(-1, 1));  // Row below, 1 in
    }
}