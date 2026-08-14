package com.arclights.entity.operator.skill;

/**
 * Reusable Arknights-style skill controller.
 * Time is measured in game ticks (60 ticks = 1 second).
 *
 * Stat bonuses use percentage values:
 *
 *  0.50  = +50%
 *  1.00  = +100%
 * -0.20  = -20%
 */
public class OperatorSkill {

    public static final int TICKS_PER_SECOND = 60;

    private final String name;
    private final SkillRecoveryType recoveryType;
    private final SkillActivationType activationType;

    private final int maxSP;
    private final int durationTicks;

    /*
     * Percentage-based stat bonuses.
     *
     * Example:
     * 0.50 = +50% ATK
     * 1.00 = +100% ATK
     */
    private final double activeAtkPercent;
    private final double activeDefensePercent;

    /*
     * Bonus applied only to the next attack.
     *
     * Example:
     * 1.00 = +100% ATK for next attack
     */
    private final double nextAttackAtkPercent;

    private final SkillEffect customEffect;

    /** Automatically activates when SP becomes full. */
    private final boolean autoCast;

    private int currentSP;
    private int autoRecoveryTimer;
    private int remainingDurationTicks;

    private boolean active;
    private boolean nextAttackPending;

    // =========================================================
    // Constructors
    // =========================================================

    /**
     * Basic constructor.
     */
    public OperatorSkill(
            String name,
            SkillRecoveryType recoveryType,
            SkillActivationType activationType,
            int maxSP,
            double activeAtkPercent,
            double nextAttackAtkPercent,
            double durationSeconds) {

        this(
            name,
            recoveryType,
            activationType,
            maxSP,
            activeAtkPercent,
            0.0,
            nextAttackAtkPercent,
            durationSeconds,
            null,
            false
        );
    }

    /**
     * Constructor with auto-cast.
     */
    public OperatorSkill(
            String name,
            SkillRecoveryType recoveryType,
            SkillActivationType activationType,
            int maxSP,
            double activeAtkPercent,
            double nextAttackAtkPercent,
            double durationSeconds,
            boolean autoCast) {

        this(
            name,
            recoveryType,
            activationType,
            maxSP,
            activeAtkPercent,
            0.0,
            nextAttackAtkPercent,
            durationSeconds,
            null,
            autoCast
        );
    }

    /**
     * Constructor with custom effect.
     */
    public OperatorSkill(
            String name,
            SkillRecoveryType recoveryType,
            SkillActivationType activationType,
            int maxSP,
            double activeAtkPercent,
            double nextAttackAtkPercent,
            double durationSeconds,
            SkillEffect customEffect) {

        this(
            name,
            recoveryType,
            activationType,
            maxSP,
            activeAtkPercent,
            0.0,
            nextAttackAtkPercent,
            durationSeconds,
            customEffect,
            false
        );
    }

    /**
     * Full constructor.
     *
     * @param activeAtkPercent
     *        ATK percentage bonus while the skill is active.
     *
     * @param activeDefensePercent
     *        DEF percentage bonus while the skill is active.
     *
     * @param nextAttackAtkPercent
     *        ATK percentage bonus applied to the next attack.
     *
     * @param durationSeconds
     *        Skill duration in seconds.
     */
    public OperatorSkill(
            String name,
            SkillRecoveryType recoveryType,
            SkillActivationType activationType,
            int maxSP,
            double activeAtkPercent,
            double activeDefensePercent,
            double nextAttackAtkPercent,
            double durationSeconds,
            SkillEffect customEffect,
            boolean autoCast) {

        if (maxSP <= 0) {
            throw new IllegalArgumentException("maxSP must be > 0");
        }

        this.name = name;
        this.recoveryType = recoveryType;
        this.activationType = activationType;

        this.maxSP = maxSP;

        this.activeAtkPercent = activeAtkPercent;
        this.activeDefensePercent = activeDefensePercent;
        this.nextAttackAtkPercent = nextAttackAtkPercent;

        this.durationTicks = Math.max(
            0,
            (int) Math.round(durationSeconds * TICKS_PER_SECOND)
        );

        this.customEffect = customEffect;
        this.autoCast = autoCast;
    }

    // =========================================================
    // Update
    // =========================================================

    /** Called once per game tick. */
    public void update() {

        // AUTO SP recovery
        if (recoveryType == SkillRecoveryType.AUTO && currentSP < maxSP) {

            autoRecoveryTimer++;

            if (autoRecoveryTimer >= TICKS_PER_SECOND) {
                autoRecoveryTimer = 0;
                addSP(1);
            }
        }

        // Duration handling
        if (active && activationType == SkillActivationType.DURATION) {

            remainingDurationTicks--;

            if (remainingDurationTicks <= 0) {

                active = false;
                remainingDurationTicks = 0;

                if (customEffect != null) {
                    customEffect.onExpired();
                }
            }
        }

        // Auto-cast
        if (autoCast) {
            activate();
        }
    }

    // =========================================================
    // SP
    // =========================================================

    /** Called after a successful operator attack. */
    public void onAttack() {

        if (recoveryType == SkillRecoveryType.OFFENSIVE) {
            addSP(1);
        }
    }

    private void addSP(int amount) {

        currentSP = Math.min(
            maxSP,
            currentSP + Math.max(0, amount)
        );
    }

    // =========================================================
    // Activation
    // =========================================================

    public boolean activate() {

        if (!isReady() || active || nextAttackPending) {
            return false;
        }

        currentSP = 0;
        autoRecoveryTimer = 0;

        if (activationType == SkillActivationType.DURATION) {

            active = true;
            remainingDurationTicks = durationTicks;

        } else {

            nextAttackPending = true;
        }

        if (customEffect != null) {
            customEffect.onActivated();
        }

        return true;
    }

    // =========================================================
    // ATK
    // =========================================================

    /**
     * Returns the ATK multiplier currently provided by the skill.
     *
     * Example:
     *
     * +50% ATK -> 1.50
     * +100% ATK -> 2.00
     * -20% ATK -> 0.80
     */
    public double getAtkMultiplier() {

        double multiplier = 1.0;

        // Duration skill
        if (activationType == SkillActivationType.DURATION && active) {

            multiplier += activeAtkPercent;
        }

        // Next attack skill
        if (activationType == SkillActivationType.NEXT_ATTACK
                && nextAttackPending) {

            multiplier += nextAttackAtkPercent;
        }

        return multiplier;
    }

    /**
     * Returns the effective ATK of the operator.
     *
     * This is the operator's actual ATK BEFORE damage calculation.
     */
    public double modifyAttack(double baseAtk) {

        double result = baseAtk * getAtkMultiplier();

        return result;
    }

    /**
     * Called after an attack has actually been performed.
     *
     * NEXT_ATTACK skills are consumed here.
     */
    public void consumeAttack() {

        if (activationType == SkillActivationType.NEXT_ATTACK
                && nextAttackPending) {

            nextAttackPending = false;

            if (customEffect != null) {
                customEffect.onExpired();
            }
        }
    }

    // =========================================================
    // DEFENSE
    // =========================================================

    /**
     * Returns the DEF multiplier provided by the skill.
     *
     * Example:
     *
     * +50% DEF -> 1.50
     * +100% DEF -> 2.00
     */
    public double getDefenseMultiplier() {

        if (activationType == SkillActivationType.DURATION
                && active) {

            return 1.0 + activeDefensePercent;
        }

        return 1.0;
    }

    /**
     * Returns the operator's effective DEF.
     */
    public double modifyDefense(double baseDefense) {

        return baseDefense * getDefenseMultiplier();
    }

    // =========================================================
    // Legacy / Damage Modification
    // =========================================================

    /**
     * Keeps custom damage effects available.
     *
     * IMPORTANT:
     * Normal ATK bonuses should NOT be implemented here anymore.
     *
     * This method is now intended for special effects such as:
     *
     * - true damage
     * - damage type conversion
     * - bonus damage against specific enemies
     * - execute effects
     * - etc.
     */
    public double modifyAttackDamage(double baseDamage) {

        double result = baseDamage;

        if (customEffect != null) {
            result = customEffect.modifyAttackDamage(result, currentSP);
        }

        return result;
    }

    // =========================================================
    // Getters
    // =========================================================

    public String getName() {
        return name;
    }

    public SkillRecoveryType getRecoveryType() {
        return recoveryType;
    }

    public SkillActivationType getActivationType() {
        return activationType;
    }

    public int getCurrentSP() {
        return currentSP;
    }

    public int getMaxSP() {
        return maxSP;
    }

    public int getRemainingDurationTicks() {
        return remainingDurationTicks;
    }

    public double getRemainingDurationSeconds() {
        return remainingDurationTicks / 60.0;
    }

    public boolean isReady() {
        return currentSP >= maxSP;
    }

    public boolean isActive() {
        return active || nextAttackPending;
    }

    public boolean isNextAttackPending() {
        return nextAttackPending;
    }

    public double getActiveAtkPercent() {
        return activeAtkPercent;
    }

    public double getActiveDefensePercent() {
        return activeDefensePercent;
    }

    public double getNextAttackAtkPercent() {
        return nextAttackAtkPercent;
    }

    public boolean isAutoCast() {
        return autoCast;
    }
}