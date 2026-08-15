package com.arclights.animation;

/**
 * Central place for the pixel-size math used to size operator/enemy sprites
 * relative to a map's tile dimensions. Kept in one spot so DeploymentManager,
 * EnemyManager, and AssetPreloader (which needs to precompute the exact same
 * sizes ahead of time, to warm SpriteAnimation's cache with the right cache
 * keys) can never drift out of sync.
 */
public final class SpriteSizing {
    /** Operators render slightly larger than a tile, matching the reference game's scale. */
    public static final double OPERATOR_SCALE = 2.35;
    /** Enemies render a bit smaller than operators. */
    public static final double ENEMY_SCALE = 2.15;

    private SpriteSizing() {
    }

    public static double operatorSize(double tileWidth, double tileHeight) {
        return Math.min(tileWidth, tileHeight) * OPERATOR_SCALE;
    }

    public static double enemySize(double tileWidth, double tileHeight) {
        return Math.min(tileWidth, tileHeight) * ENEMY_SCALE;
    }
}
