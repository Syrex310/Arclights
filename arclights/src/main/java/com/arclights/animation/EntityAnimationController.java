package com.arclights.animation;

import com.arclights.entity.GameEntity;
import com.arclights.entity.enemy.Enemy;
import com.arclights.entity.operator.Operator;

import javafx.scene.paint.Color;

public final class EntityAnimationController {
    private final AnimatedSprite sprite;
    private final GameEntity entity;
    private final boolean enemy;
    private final String spriteId;
    private int attackTicksRemaining;

    public EntityAnimationController(GameEntity entity, String spriteId, boolean enemy,
                                     double fallbackRadius, Color fallbackColor,
                                     double spriteWidth, double spriteHeight) {
        this.entity = entity;
        this.enemy = enemy;
        this.spriteId = spriteId.toLowerCase();
        this.sprite = new AnimatedSprite(fallbackRadius, fallbackColor);
        this.sprite.setFitSize(spriteWidth, spriteHeight);
        loadAnimations();
    }

    private void loadAnimations() {
        String group = enemy ? "enemies" : "operators";

        load(AnimationState.IDLE, group, 1, true);
        load(AnimationState.WALK, group, 1, true);
        load(AnimationState.ATTACK, group, 1, false);
        load(AnimationState.DEATH, group, 1, false);
        sprite.play(AnimationState.IDLE);
    }

    private void load(AnimationState state, String group, double ticksPerFrame, boolean loop) {
        String path = "/sprites/" + group + "/" + spriteId + "/" + state.name().toLowerCase();
        sprite.setAnimation(state, SpriteAnimation.load(path, ticksPerFrame, loop));
    }

    public AnimatedSprite getSprite() { return sprite; }

    public void triggerAttack() {
        attackTicksRemaining = 12;
        sprite.play(AnimationState.ATTACK);
    }

    public void update() {
        if (!entity.isAlive()) {
            sprite.play(AnimationState.DEATH);
            sprite.update();
            return;
        }

        if (attackTicksRemaining > 0) {
            attackTicksRemaining--;
            sprite.play(AnimationState.ATTACK);
        } else if (enemy) {
            Enemy e = (Enemy) entity;
            sprite.play(e.isBlocked() ? AnimationState.IDLE : AnimationState.WALK);
            sprite.setFacingWest(false);
        } else {
            Operator op = (Operator) entity;
            sprite.setFacingWest(op.getFacing() == Operator.Direction.WEST);
            sprite.play(AnimationState.IDLE);
        }
        sprite.update();
    }
}
