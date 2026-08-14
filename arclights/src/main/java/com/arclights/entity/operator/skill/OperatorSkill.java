package com.arclights.entity.operator.skill;

/**
 * Reusable Arknights-style skill controller.
 * Time is measured in game ticks (60 ticks = 1 second).
 */
public class OperatorSkill {
    public static final int TICKS_PER_SECOND = 60;

    private final String name;
    private final SkillRecoveryType recoveryType;
    private final SkillActivationType activationType;
    private final int maxSP;
    private final int durationTicks;
    private final double activeAtkMultiplier;
    private final double nextAttackDamageMultiplier;
    private final SkillEffect customEffect;
    /** When true, the skill fires itself the instant it reaches full SP - no player click needed. */
    private final boolean autoCast;

    private int currentSP;
    private int autoRecoveryTimer;
    private int remainingDurationTicks;
    private boolean active;
    private boolean nextAttackPending;

    public OperatorSkill(
            String name,
            SkillRecoveryType recoveryType,
            SkillActivationType activationType,
            int maxSP,
            double activeAtkMultiplier,
            double nextAttackDamageMultiplier,
            double durationSeconds) {
        this(name, recoveryType, activationType, maxSP, activeAtkMultiplier,
                nextAttackDamageMultiplier, durationSeconds, null, false);
    }

    /**
     * Same as the 7-arg constructor, plus {@code autoCast}: pass {@code true}
     * to have this skill activate itself automatically the moment it reaches
     * full SP, instead of waiting for the player to click it. Defaults to
     * {@code false} (manual activation) everywhere else.
     */
    public OperatorSkill(
            String name,
            SkillRecoveryType recoveryType,
            SkillActivationType activationType,
            int maxSP,
            double activeAtkMultiplier,
            double nextAttackDamageMultiplier,
            double durationSeconds,
            boolean autoCast) {
        this(name, recoveryType, activationType, maxSP, activeAtkMultiplier,
                nextAttackDamageMultiplier, durationSeconds, null, autoCast);
    }

    public OperatorSkill(
            String name,
            SkillRecoveryType recoveryType,
            SkillActivationType activationType,
            int maxSP,
            double activeAtkMultiplier,
            double nextAttackDamageMultiplier,
            double durationSeconds,
            SkillEffect customEffect) {
        this(name, recoveryType, activationType, maxSP, activeAtkMultiplier,
                nextAttackDamageMultiplier, durationSeconds, customEffect, false);
    }

    /**
     * Full constructor. See the {@code autoCast}-only overload above for what
     * {@code autoCast} does.
     */
    public OperatorSkill(
            String name,
            SkillRecoveryType recoveryType,
            SkillActivationType activationType,
            int maxSP,
            double activeAtkMultiplier,
            double nextAttackDamageMultiplier,
            double durationSeconds,
            SkillEffect customEffect,
            boolean autoCast) {
        if (maxSP <= 0) throw new IllegalArgumentException("maxSP must be > 0");
        this.name = name;
        this.recoveryType = recoveryType;
        this.activationType = activationType;
        this.maxSP = maxSP;
        this.activeAtkMultiplier = activeAtkMultiplier;
        this.nextAttackDamageMultiplier = nextAttackDamageMultiplier;
        this.durationTicks = Math.max(0, (int) Math.round(durationSeconds * TICKS_PER_SECOND));
        this.customEffect = customEffect;
        this.autoCast = autoCast;
    }

    /** Called once per game tick. */
    public void update() {
        if (recoveryType == SkillRecoveryType.AUTO && currentSP < maxSP) {
            autoRecoveryTimer++;
            if (autoRecoveryTimer >= TICKS_PER_SECOND) {
                autoRecoveryTimer = 0;
                addSP(1);
            }
        }

        if (active && activationType == SkillActivationType.DURATION) {
            remainingDurationTicks--;
            if (remainingDurationTicks <= 0) {
                active = false;
                remainingDurationTicks = 0;
                if (customEffect != null) customEffect.onExpired();
            }
        }

        // Fire the moment SP is full if this skill is set to auto-cast.
        // activate() already no-ops unless isReady() && !active && !nextAttackPending,
        // so it's safe to call unconditionally every tick.
        if (autoCast) {
            activate();
        }
    }

    /** Call after a successful operator attack. */
    public void onAttack() {
        if (recoveryType == SkillRecoveryType.OFFENSIVE) {
            addSP(1);
        }
    }

    public boolean activate() {
        if (!isReady() || active || nextAttackPending) return false;

        currentSP = 0;
        autoRecoveryTimer = 0;

        if (activationType == SkillActivationType.DURATION) {
            active = true;
            remainingDurationTicks = durationTicks;
        } else {
            nextAttackPending = true;
        }

        if (customEffect != null) customEffect.onActivated();
        return true;
    }

    /**
     * Modifies attack damage according to the currently active skill.
     * For NEXT_ATTACK skills the skill is consumed by this call.
     */
    public double modifyAttackDamage(double baseDamage) {
        double result = baseDamage;

        if (activationType == SkillActivationType.DURATION && active) {
            result *= activeAtkMultiplier;
        }

        if (activationType == SkillActivationType.NEXT_ATTACK && nextAttackPending) {
            result *= nextAttackDamageMultiplier;
            nextAttackPending = false;
            if (customEffect != null) customEffect.onExpired();
        }

        if (customEffect != null) {
            result = customEffect.modifyAttackDamage(result, currentSP);
        }
        return result;
    }

    private void addSP(int amount) {
        currentSP = Math.min(maxSP, currentSP + Math.max(0, amount));
    }

    public String getName() { return name; }
    public SkillRecoveryType getRecoveryType() { return recoveryType; }
    public SkillActivationType getActivationType() { return activationType; }
    public int getCurrentSP() { return currentSP; }
    public int getMaxSP() { return maxSP; }
    public int getRemainingDurationTicks() { return remainingDurationTicks; }
    public double getRemainingDurationSeconds() { return remainingDurationTicks / 60.0; }
    public boolean isReady() { return currentSP >= maxSP; }
    public boolean isActive() { return active || nextAttackPending; }
    public boolean isNextAttackPending() { return nextAttackPending; }
    public double getActiveAtkMultiplier() { return activeAtkMultiplier; }
    public double getNextAttackDamageMultiplier() { return nextAttackDamageMultiplier; }
    public boolean isAutoCast() { return autoCast; }
}
