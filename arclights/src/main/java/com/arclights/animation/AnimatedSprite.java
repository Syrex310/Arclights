package com.arclights.animation;

import java.util.EnumMap;
import java.util.Map;

import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public final class AnimatedSprite {
    private final StackPane node = new StackPane();
    private final ImageView imageView = new ImageView();
    private final Circle fallback;
    private final Map<AnimationState, SpriteAnimation> animations = new EnumMap<>(AnimationState.class);
    private AnimationState currentState = AnimationState.IDLE;
    private boolean facingWest;

    public AnimatedSprite(double fallbackRadius, Color fallbackColor) {
        fallback = new Circle(fallbackRadius, fallbackColor);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(false);
        node.getChildren().addAll(fallback, imageView);
        node.setPickOnBounds(false);
        imageView.setVisible(false);
    }

    public Node getNode() { return node; }
    public ImageView getImageView() { return imageView; }

    public void setAnimation(AnimationState state, SpriteAnimation animation) {
        animations.put(state, animation);
        if (state == currentState) refreshFrame();
    }

    public void play(AnimationState state) {
        if (state == currentState) return;
        currentState = state;
        SpriteAnimation animation = animations.get(state);
        if (animation != null) animation.reset();
        refreshFrame();
    }

    public AnimationState getCurrentState() { return currentState; }

    public void setFacingWest(boolean west) {
        facingWest = west;
        imageView.setScaleX(west ? -1 : 1);
    }

    public void update() {
        SpriteAnimation animation = animations.get(currentState);
        if (animation == null || !animation.hasFrames()) return;
        animation.update();
        refreshFrame();
    }

    private void refreshFrame() {
        SpriteAnimation animation = animations.get(currentState);
        if (animation == null || !animation.hasFrames()) {
            imageView.setVisible(false);
            fallback.setVisible(true);
            return;
        }
        imageView.setImage(animation.getCurrentFrame());
        imageView.setVisible(true);
        fallback.setVisible(false);
    }

    public boolean isCurrentAnimationFinished() {
        SpriteAnimation animation = animations.get(currentState);
        return animation != null && animation.isFinished();
    }

    public void setFitSize(double width, double height) {
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
    }
}
