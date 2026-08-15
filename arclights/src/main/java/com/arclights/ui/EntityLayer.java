package com.arclights.ui;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleSupplier;

import javafx.scene.Node;
import javafx.scene.layout.Pane;

/**
 * Dedicated container for operator/enemy sprites that keeps them ordered by
 * on-map Y position, i.e. a simple painter's algorithm: an entity standing
 * lower on the map (larger Y) is drawn after - and therefore appears in
 * front of - an entity standing higher up (smaller Y). This gives correct
 * visual overlap when a sniper's row is "behind" a defender it stands
 * next to, or when an enemy walks past an operator on an adjacent tile.
 *
 * Usage:
 *  - Add getPane() to the scene graph once (e.g. right above the map tiles).
 *  - track() every sprite node as it's created, with a supplier that always
 *    returns that entity's *current* Y (e.g. entity::getY).
 *  - untrack() the node when the entity/sprite is removed.
 *  - Call sortByDepth() once per simulation tick, after entity positions
 *    have been updated, so draw order stays correct as things move.
 */
public class EntityLayer {
    private final Pane pane = new Pane();
    private final Map<Node, DoubleSupplier> depthKeys = new IdentityHashMap<>();

    public EntityLayer() {
        pane.setMouseTransparent(false);
    }

    public Pane getPane() {
        return pane;
    }

    /** Registers a sprite node for depth sorting and adds it to the layer. */
    public void track(Node node, DoubleSupplier ySupplier) {
        depthKeys.put(node, ySupplier);
        pane.getChildren().add(node);
    }

    /** Removes a sprite node from the layer and stops tracking its depth. */
    public void untrack(Node node) {
        depthKeys.remove(node);
        pane.getChildren().remove(node);
    }

    /**
     * Re-orders the tracked sprites so lower (larger-Y) entities render on
     * top of higher (smaller-Y) ones. JavaFX draws Pane children in list
     * order (later entries appear in front), so this sorts ascending by Y.
     * Cheap enough at this game's entity counts to run every tick.
     */
    public void sortByDepth() {
        if (depthKeys.isEmpty()) return;

        List<Node> sorted = new ArrayList<>(pane.getChildren());
        sorted.sort(Comparator.comparingDouble(n -> depthKeys.getOrDefault(n, () -> 0.0).getAsDouble()));
        pane.getChildren().setAll(sorted);
    }
}
