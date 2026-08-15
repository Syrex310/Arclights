package com.arclights.entity.operator;

import java.util.List;

import com.arclights.entity.enemy.Enemy;
import com.arclights.entity.operator.skill.OperatorSkill;
import com.arclights.entity.operator.skill.SkillActivationType;
import com.arclights.entity.operator.skill.SkillRecoveryType;

import javafx.geometry.Point2D;

public class Supporter extends Operator {
    public static final int DEPLOY_COST = 8;

    /**
     * Fraction of ATK healed to every ally in range, per tick (0.2% ATK per
     * tick -> 60 ticks/sec * 0.002 = 12% ATK/sec).
     */
    private static final double HEAL_PERCENT_PER_TICK = 0.002;

    public Supporter(double gridX, double gridY) {
        super(gridX, gridY, 1603, 368, 1, AttackType.ARTS, 78, 0, 233, false, DEPLOY_COST);

        setSkill(new OperatorSkill(
            "Wish of Burial Beyond the Light",
            SkillRecoveryType.AUTO,
            SkillActivationType.DURATION,
            60,
            0.5,
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

    /**
     * Supporters never attack enemies. Every tick, they heal ALL allies
     * currently in range (not just the most-injured one) for 0.2% of this
     * Supporter's ATK per tick (12% ATK/sec at 60 ticks/sec). There is no
     * cooldown gate here and {@link #resetAttackCooldown()} is never called,
     * so this simply fires on every {@code update()} tick.
     */
    @Override
    protected void performAction(List<Enemy> activeEnemies, List<Operator> allies) {
        List<Operator> targets = findAllHealTargetsInGridRange(allies);
        if (targets.isEmpty()) return;

        double healAmount = getAtk() * HEAL_PERCENT_PER_TICK;

        for (Operator target : targets) {
            target.setHp(Math.min(target.getMaxHp(), target.getHp() + healAmount));
        }
    }
}