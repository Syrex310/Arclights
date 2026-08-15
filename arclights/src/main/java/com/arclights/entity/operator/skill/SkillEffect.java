package com.arclights.entity.operator.skill;

/**
 * Optional extension point for future operator-specific skill behaviour.
 * Return the modified damage when the operator attacks.
 */
@FunctionalInterface
public interface SkillEffect {
    double modifyAttackDamage(double baseDamage, int currentSP);

    default void onActivated() {}
    default void onExpired() {}
}
