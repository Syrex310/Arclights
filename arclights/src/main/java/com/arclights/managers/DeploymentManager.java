package com.arclights.managers;

import java.util.ArrayList;
import java.util.List;

import com.arclights.models.Defender;
import com.arclights.models.Enemy;
import com.arclights.models.GameMap;
import com.arclights.models.Operator;
import com.arclights.models.Sniper;
import com.arclights.models.Tile;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class DeploymentManager {
    public enum SelectionState { NONE, DRAGGING_SNIPER, DRAGGING_DEFENDER, SELECTING_DIRECTION }

    private final List<Operator> activeOperators = new ArrayList<>();
    private final List<Rectangle> rangePreviewNodes = new ArrayList<>();
    private final Pane root;

    private SelectionState currentState = SelectionState.NONE;
    private Tile pendingTile;
    private int pendingCol;
    private int pendingRow;
    private Operator pendingOperator;

    // Visual drag indicators
    private final Circle dragGhost;
    private Circle finalOpSprite;
    private Polygon finalDirectionArrow;

    // Dynamic swipe tracking coordinates
    private double directionStartX;
    private double directionStartY;

    public DeploymentManager(Pane root) {
        this.root = root;
        
        // Setup hidden tracking ghost
        this.dragGhost = new Circle(20, Color.rgb(255, 255, 255, 0.6));
        this.dragGhost.setVisible(false);
        this.dragGhost.setMouseTransparent(true);
        this.root.getChildren().add(this.dragGhost);
    }

    public SelectionState getCurrentState() { return currentState; }

    public void startDrag(SelectionState dragType) {
        this.currentState = dragType;
        dragGhost.setFill(dragType == SelectionState.DRAGGING_DEFENDER ? Color.rgb(0, 0, 255, 0.6) : Color.rgb(0, 255, 0, 0.6));
        dragGhost.setVisible(true);
    }

    public void updateDragPosition(double x, double y, GameMap gameMap) {
        if (currentState == SelectionState.NONE || currentState == SelectionState.SELECTING_DIRECTION) return;
        
        dragGhost.setCenterX(x);
        dragGhost.setCenterY(y);

        // Map pixel hover back to grid row/col coordinates
        int col = (int) ((x - 50) / 62);
        int row = (int) ((y - 50) / 62);

        clearRangePreview();

        // If hovering over a valid tile space, show a temporary range preview layout
        if (row >= 0 && row < gameMap.getRows() && col >= 0 && col < gameMap.getCols()) {
            Tile tile = gameMap.getTile(row, col);
            if (!tile.isOccupied()) {
                Operator tempOp = null;
                if (currentState == SelectionState.DRAGGING_SNIPER && tile.canPlaceRanged()) {
                    tempOp = new Sniper(col, row);
                } else if (currentState == SelectionState.DRAGGING_DEFENDER && (tile.canPlaceMelee() || tile.isEnemyPath())) {
                    tempOp = new Defender(col, row);
                }

                if (tempOp != null) {
                    showRangePreview(tempOp);
                }
            }
        }
    }

    public void handleRelease(double x, double y, GameMap gameMap) {
        dragGhost.setVisible(false);
        if (currentState == SelectionState.NONE || currentState == SelectionState.SELECTING_DIRECTION) return;

        int col = (int) ((x - 50) / 62);
        int row = (int) ((y - 50) / 62);

        if (row >= 0 && row < gameMap.getRows() && col >= 0 && col < gameMap.getCols()) {
            Tile tile = gameMap.getTile(row, col);
            
            if (!tile.isOccupied()) {
                if (currentState == SelectionState.DRAGGING_SNIPER && tile.canPlaceRanged()) {
                    pendingOperator = new Sniper(col, row);
                } else if (currentState == SelectionState.DRAGGING_DEFENDER && (tile.canPlaceMelee() || tile.isEnemyPath())) {
                    pendingOperator = new Defender(col, row);
                }

                if (pendingOperator != null) {
                    pendingTile = tile;
                    pendingCol = col;
                    pendingRow = row;
                    currentState = SelectionState.SELECTING_DIRECTION;

                    // Initialize swipe tracking coords to unit tile's center as a healthy default/fallback
                    this.directionStartX = pendingOperator.getX();
                    this.directionStartY = pendingOperator.getY();

                    // Lock preliminary unit layout circle in place
                    finalOpSprite = new Circle(pendingOperator.isGround() ? 20 : 18);
                    finalOpSprite.setFill(pendingOperator.isGround() ? Color.BLUE : Color.GREEN);
                    finalOpSprite.setCenterX(pendingOperator.getX());
                    finalOpSprite.setCenterY(pendingOperator.getY());
                    root.getChildren().add(finalOpSprite);

                    // Add navigation direction arrow asset
                    finalDirectionArrow = new Polygon();
                    finalDirectionArrow.getPoints().addAll(new Double[]{ 0.0, -8.0, -6.0, 4.0, 6.0, 4.0 });
                    finalDirectionArrow.setFill(Color.GOLD);
                    finalDirectionArrow.setTranslateX(pendingOperator.getX());
                    finalDirectionArrow.setTranslateY(pendingOperator.getY());
                    root.getChildren().add(finalDirectionArrow);

                    showRangePreview(pendingOperator);
                    return;
                }
            }
        }

        // Reset if released over out-of-bounds or invalid tile environments
        currentState = SelectionState.NONE;
        clearRangePreview();
    }

    /**
     * Set the origin point of the swipe selection when clicking/pressing during Phase 2.
     */
    public void setDirectionDragStart(double x, double y) {
        this.directionStartX = x;
        this.directionStartY = y;
    }

    /**
     * Calculates the drag angle relative to the start coordinates to update the operator's orientation.
     */
    public void handleDirectionDrag(double mouseX, double mouseY) {
        if (currentState != SelectionState.SELECTING_DIRECTION || pendingOperator == null) return;

        double dx = mouseX - directionStartX;
        double dy = mouseY - directionStartY;

        // Enforce a tiny deadzone threshold before swiping kicks in
        if (Math.sqrt(dx * dx + dy * dy) < 15) return;

        Operator.Direction dynamicDir;
        if (Math.abs(dx) > Math.abs(dy)) {
            dynamicDir = (dx > 0) ? Operator.Direction.EAST : Operator.Direction.WEST;
        } else {
            dynamicDir = (dy > 0) ? Operator.Direction.SOUTH : Operator.Direction.NORTH;
        }

        pendingOperator.setFacing(dynamicDir);
        
        switch (dynamicDir) {
            case NORTH: finalDirectionArrow.setRotate(0); break;
            case EAST:  finalDirectionArrow.setRotate(90); break;
            case SOUTH: finalDirectionArrow.setRotate(180); break;
            case WEST:  finalDirectionArrow.setRotate(270); break;
        }

        showRangePreview(pendingOperator);
    }

    public void confirmDeployment() {
        if (currentState != SelectionState.SELECTING_DIRECTION || pendingOperator == null) return;

        pendingTile.setOccupied(true);
        activeOperators.add(pendingOperator);

        // Reset state values cleanly
        currentState = SelectionState.NONE;
        pendingOperator = null;
        pendingTile = null;
        finalOpSprite = null;
        finalDirectionArrow = null;
        clearRangePreview();
        System.out.println("Deployment bound locked successfully.");
    }

    private void showRangePreview(Operator op) {
        clearRangePreview();
        List<Point2D> tiles = op.getAbsoluteRangeTiles();
        for (Point2D tilePos : tiles) {
            Rectangle rect = new Rectangle(60, 60);
            rect.setFill(Color.rgb(255, 69, 0, 0.3));
            rect.setX(tilePos.getX() * 62 + 50);
            rect.setY(tilePos.getY() * 62 + 50);
            rect.setMouseTransparent(true);
            
            root.getChildren().add(rect);
            rangePreviewNodes.add(rect);
        }
    }

    private void clearRangePreview() {
        for (Rectangle rect : rangePreviewNodes) {
            root.getChildren().remove(rect);
        }
        rangePreviewNodes.clear();
    }

    public void update(List<Enemy> activeEnemies) {
        for (Operator op : activeOperators) {
            op.update(activeEnemies);
        }
    }
}