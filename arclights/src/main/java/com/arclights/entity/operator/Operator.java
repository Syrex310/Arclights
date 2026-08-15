package com.arclights.entity.operator;

import java.util.ArrayList;
import java.util.List;

import com.arclights.entity.GameEntity;
import com.arclights.entity.enemy.Enemy;
import com.arclights.entity.operator.skill.OperatorSkill;

import javafx.geometry.Point2D;

public class Operator extends GameEntity {

    public enum Direction {
        NORTH, EAST, SOUTH, WEST
    }

    private final int gridX;
    private final int gridY;

    private Direction facing;

    private int attackCooldownTimer;
    private boolean attackTriggered;

    private OperatorSkill skill;

    private final int deployCost;

    private final List<Enemy> blockedEnemies = new ArrayList<>();

    protected List<Point2D> relativeRangeOffsets = new ArrayList<>();

    // =========================================================
    // Constructor
    // =========================================================

    // Constructor receives true pixel positions directly from DeploymentManager
    public Operator(
            int gridX,
            int gridY,
            double pixelX,
            double pixelY,
            double hp,
            double atk,
            int blockCount,
            AttackType attackType,
            double attackInterval,
            double resistance,
            double defense,
            boolean isGround,
            int deployCost) {

        super(
            pixelX,
            pixelY,
            hp,
            atk,
            blockCount,
            attackType,
            attackInterval,
            resistance,
            defense,
            true,
            isGround
        );

        this.gridX = gridX;
        this.gridY = gridY;

        this.attackCooldownTimer = 0;

        this.facing = Direction.EAST;

        this.skill = null;

        this.deployCost = deployCost;
    }

    // Overloaded constructor for fallback / default pixel calculation
    public Operator(
            double gridX,
            double gridY,
            double hp,
            double atk,
            int blockCount,
            AttackType attackType,
            double attackInterval,
            double resistance,
            double defense,
            boolean isGround,
            int deployCost) {

        this(
            (int) gridX,
            (int) gridY,
            gridX * 64 + 32,
            gridY * 64 + 32,
            hp,
            atk,
            blockCount,
            attackType,
            attackInterval,
            resistance,
            defense,
            isGround,
            deployCost
        );
    }

    // =========================================================
    // Deployment
    // =========================================================

    /**
     * DP cost required to deploy this operator,
     * mirroring Arknights' deployment-point system.
     */
    public int getDeployCost() {
        return deployCost;
    }

    // =========================================================
    // Direction
    // =========================================================

    public void setFacing(Direction facing) {
        this.facing = facing;
    }

    public Direction getFacing() {
        return facing;
    }

    // =========================================================
    // Grid
    // =========================================================

    public int getGridX() {
        return gridX;
    }

    public int getGridY() {
        return gridY;
    }

    // =========================================================
    // Range
    // =========================================================

    public List<Point2D> getAbsoluteRangeTiles() {

        List<Point2D> absoluteTiles = new ArrayList<>();

        for (Point2D offset : relativeRangeOffsets) {

            double dx = offset.getX();
            double dy = offset.getY();

            double rotatedX = dx;
            double rotatedY = dy;

            // Correct 2D rotation matrix relative to EAST (0 degrees)
            switch (facing) {

                case NORTH:
                    rotatedX = dy;
                    rotatedY = -dx;
                    break;

                case SOUTH:
                    rotatedX = -dy;
                    rotatedY = dx;
                    break;

                case WEST:
                    rotatedX = -dx;
                    rotatedY = -dy;
                    break;

                case EAST:
                default:
                    rotatedX = dx;
                    rotatedY = dy;
                    break;
            }

            absoluteTiles.add(
                new Point2D(
                    gridX + rotatedX,
                    gridY + rotatedY
                )
            );
        }

        return absoluteTiles;
    }

    // =========================================================
    // Blocking
    // =========================================================

    public int getRemainingBlockCount() {

        int usedBlock = 0;

        for (Enemy enemy : blockedEnemies) {
            usedBlock += enemy.getBlockCount();
        }

        return Math.max(
            0,
            getBlockCount() - usedBlock
        );
    }

    // =========================================================
    // Update
    // =========================================================

    public void update(List<Enemy> activeEnemies) {
        update(activeEnemies, java.util.Collections.emptyList());
    }

    /**
     * @param activeEnemies enemies currently on the field
     * @param allies this operator's own side currently deployed
     */
    public void update(
            List<Enemy> activeEnemies,
            List<Operator> allies) {

        // Update skill first
        if (skill != null) {
            skill.update();
        }

        // =====================================================
        // Death cleanup
        // =====================================================

        if (!isAlive()) {

            for (Enemy enemy : blockedEnemies) {
                enemy.setBlocked(false);
                enemy.setBlockedBy(null);
            }

            blockedEnemies.clear();

            return;
        }

        // Remove dead enemies from blocking list
        blockedEnemies.removeIf(
            enemy -> !enemy.isAlive()
        );

        // =====================================================
        // Blocking
        // =====================================================

        if (isGround()) {

            for (Enemy enemy : activeEnemies) {

                if (!enemy.isAlive() || enemy.isBlocked()) {
                    continue;
                }

                // Check if enemy is on the same tile
                boolean isSameTile =
                    enemy.getCurrentGridX() == this.gridX
                    &&
                    enemy.getCurrentGridY() == this.gridY;

                if (isSameTile) {

                    if (getRemainingBlockCount()
                            >= enemy.getBlockCount()) {

                        blockedEnemies.add(enemy);

                        enemy.setBlocked(true);
                        enemy.setBlockedBy(this);

                        System.out.println(
                            "Enemy blocked at grid ("
                            + gridX
                            + ", "
                            + gridY
                            + ")! Remaining Block: "
                            + getRemainingBlockCount()
                        );
                    }
                }
            }
        }

        // =====================================================
        // Attack cooldown
        // =====================================================

        if (attackCooldownTimer > 0) {

            attackCooldownTimer--;

        } else {

            performAction(activeEnemies, allies);
        }
    }

    // =========================================================
    // Combat
    // =========================================================

    /**
     * Executes this operator's per-cooldown action.
     *
     * Default behaviour:
     *
     * 1. Attack an enemy currently being blocked.
     * 2. Otherwise find an enemy inside attack range.
     */
    protected void performAction(
            List<Enemy> activeEnemies,
            List<Operator> allies) {

        Enemy target = null;

        // Prefer blocked enemy
        if (!blockedEnemies.isEmpty()) {

            target = blockedEnemies.get(0);

        } else {

            target = findTargetInGridRange(activeEnemies);
        }

        if (target != null) {
            double attack = getEffectiveAtk();
            double damage = attack;

            if (skill != null) {
                damage = skill.modifyAttackDamage(damage);
            }

            target.takeDamage(
                damage,
                getAttackType()
            );

            if (skill != null) {

                // Offensive SP recovery
                skill.onAttack();

                // Consume NEXT_ATTACK skill if necessary
                skill.consumeAttack();
            }

            markAttackTriggered();

            System.out.println(
                "Operator attacked enemy!"
                + " | ATK: " + attack
                + " | Damage: " + damage
                + " | Enemy HP: " + target.getHp()
            );

            resetAttackCooldown();
        }
    }

    public double getEffectiveAtk() {

        double atk = getAtk();

        if (skill != null) {
            atk = skill.modifyAttack(atk);
        }

        return atk;
    }

    public double getEffectiveDefense() {

        double defense = getDefense();

        if (skill != null) {
            defense = skill.modifyDefense(defense);
        }

        return defense;
    }

    @Override
    public void takeDamage(
            double damage,
            AttackType attackType) {

        if (!isAlive()) {
            return;
        }

        double mitigation;

        if (attackType == AttackType.PHYSICAL) {

            mitigation = getEffectiveDefense();

            double remainingHp =
                getHp()
                - Math.max(damage - mitigation, 0);

            remainingHp = Math.max(
                remainingHp,
                0
            );

            setHp(remainingHp);

            if (remainingHp <= 0) {
                setIsAlive(false);
            }

            return;
        }

        if (attackType == AttackType.ARTS) {

            mitigation = getResistance();

            double remainingHp =
                getHp()
                - damage * (1 - mitigation);

            remainingHp = Math.max(
                remainingHp,
                0
            );

            setHp(remainingHp);

            if (remainingHp <= 0) {
                setIsAlive(false);
            }
        }
    }

    protected void markAttackTriggered() {
        attackTriggered = true;
    }

    protected void resetAttackCooldown() {
        attackCooldownTimer =
            (int) getAttackInterval();
    }

    public void setSkill(OperatorSkill skill) {
        this.skill = skill;
    }

    public OperatorSkill getSkill() {
        return skill;
    }

    public boolean activateSkill() {

        return skill != null
            && skill.activate();
    }

    public void retreat() {

        for (Enemy enemy : blockedEnemies) {

            enemy.setBlocked(false);
            enemy.setBlockedBy(null);
        }

        blockedEnemies.clear();

        setIsAlive(false);
    }

    public boolean consumeAttackTriggered() {

        boolean triggered = attackTriggered;

        attackTriggered = false;

        return triggered;
    }

    private Enemy findTargetInGridRange(
            List<Enemy> activeEnemies) {

        List<Point2D> targetTiles =
            getAbsoluteRangeTiles();

        for (Enemy enemy : activeEnemies) {

            if (!enemy.isAlive()) {
                continue;
            }

            for (Point2D tile : targetTiles) {

                if (
                    (int) tile.getX()
                        == enemy.getCurrentGridX()
                    &&
                    (int) tile.getY()
                        == enemy.getCurrentGridY()
                ) {

                    return enemy;
                }
            }
        }

        return null;
    }

    protected Operator findHealTargetInGridRange(
            List<Operator> allies) {

        List<Point2D> targetTiles =
            getAbsoluteRangeTiles();

        Operator best = null;

        double lowestHpRatio =
            Double.MAX_VALUE;

        for (Operator ally : allies) {

            if (
                ally == null
                || !ally.isAlive()
                || ally.getHp() >= ally.getMaxHp()
            ) {
                continue;
            }

            for (Point2D tile : targetTiles) {

                if (
                    (int) tile.getX()
                        == ally.getGridX()
                    &&
                    (int) tile.getY()
                        == ally.getGridY()
                ) {

                    double hpRatio =
                        ally.getHp()
                        / ally.getMaxHp();

                    if (hpRatio < lowestHpRatio) {

                        lowestHpRatio = hpRatio;

                        best = ally;
                    }

                    break;
                }
            }
        }

        return best;
    }

    /**
     * Like {@link #findHealTargetInGridRange}, but returns every ally inside
     * this operator's range that isn't already at full HP, instead of just
     * the single most-injured one. Used by AoE-style healers (e.g.
     * {@link Supporter}) that heal everyone in range at once rather than
     * picking one target per proc.
     */
    protected List<Operator> findAllHealTargetsInGridRange(
            List<Operator> allies) {

        List<Point2D> targetTiles =
            getAbsoluteRangeTiles();

        List<Operator> targets = new ArrayList<>();

        for (Operator ally : allies) {

            if (
                ally == null
                || !ally.isAlive()
                || ally.getHp() >= ally.getMaxHp()
            ) {
                continue;
            }

            for (Point2D tile : targetTiles) {

                if (
                    (int) tile.getX()
                        == ally.getGridX()
                    &&
                    (int) tile.getY()
                        == ally.getGridY()
                ) {

                    targets.add(ally);
                    break;
                }
            }
        }

        return targets;
    }

    @Override
    public void update() {
        // Operator uses update(activeEnemies, allies)
    }
}