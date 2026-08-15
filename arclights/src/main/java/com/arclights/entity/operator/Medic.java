package com.arclights.entity.operator;

import java.util.List;

import com.arclights.entity.enemy.Enemy;
import com.arclights.entity.operator.skill.OperatorSkill;
import com.arclights.entity.operator.skill.SkillActivationType;
import com.arclights.entity.operator.skill.SkillRecoveryType;

import javafx.geometry.Point2D;

public class Medic extends Operator {
    /** DP cost to deploy a Defender, accessible without instantiating one. */
    public static final int DEPLOY_COST = 18;

    public Medic(double gridX, double gridY) {
        super(gridX, gridY, 1345, 488, 1, AttackType.ARTS, 171, 0, 122, false, DEPLOY_COST);

        setSkill(new OperatorSkill(
            "Deep Healing",
            SkillRecoveryType.AUTO,
            SkillActivationType.DURATION,
            15,
            1,
            0,
            0,  
            30,
            null,
            false
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
        this.relativeRangeOffsets.add(new Point2D(3, -1)); // Row above, 3 out
        this.relativeRangeOffsets.add(new Point2D(3, 0));  // Row center, 3 out
        this.relativeRangeOffsets.add(new Point2D(3, 1));  // Row below, 3 out
    }

    /**
     * Medics don't attack: every cooldown, heal the most-injured ally within
     * range (see {@link Operator#findHealTargetInGridRange}) for an amount
     * equal to this Medic's ATK, instead of damaging an enemy. Does nothing
     * (and keeps its cooldown ready) if no ally in range needs healing.
     */
    @Override
    protected void performAction(List<Enemy> activeEnemies, List<Operator> allies) {
        Operator target = findHealTargetInGridRange(allies);
        if (target == null) return;

        double healAmount = getAtk();
        target.setHp(Math.min(target.getMaxHp(), target.getHp() + healAmount));

        markAttackTriggered();
        resetAttackCooldown();
    }

}