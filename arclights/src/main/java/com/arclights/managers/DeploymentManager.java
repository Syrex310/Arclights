package com.arclights.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arclights.animation.EntityAnimationController;
import com.arclights.entity.enemy.Enemy;
import com.arclights.entity.operator.Defender;
import com.arclights.entity.operator.Operator;
import com.arclights.entity.operator.Sniper;
import com.arclights.models.GameMap;
import com.arclights.models.Tile;
import com.arclights.ui.MapRenderer;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class DeploymentManager {
    public enum SelectionState { NONE, DRAGGING_SNIPER, DRAGGING_DEFENDER, SELECTING_DIRECTION }

    private final List<Operator> activeOperators = new ArrayList<>();
    private final Map<Operator, EntityAnimationController> animations = new HashMap<>();
    private final List<Rectangle> rangePreviewNodes = new ArrayList<>();
    private final Pane root;

    // Dynamic map alignment layout metrics
    private double tileWidth;
    private double tileHeight;
    private double paddingX;
    private double paddingY;
    private double offsetX;
    private double offsetY;

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
        
        // Default fallbacks in case layout info isn't passed immediately
        this.tileWidth = 62;
        this.tileHeight = 62;
        this.paddingX = 2;
        this.paddingY = 2;
        this.offsetX = 50;
        this.offsetY = 50;

        // Setup hidden tracking ghost
        this.dragGhost = new Circle(20, Color.rgb(255, 255, 255, 0.6));
        this.dragGhost.setVisible(false);
        this.dragGhost.setMouseTransparent(true);
        this.root.getChildren().add(this.dragGhost);
    }

    /**
     * Call this when map layout metrics are calculated by MapRenderer.
     */
    public void updateMapLayout(MapRenderer.RenderResult renderResult) {
        this.tileWidth = renderResult.tileWidth;
        this.tileHeight = renderResult.tileHeight;
        this.paddingX = renderResult.paddingX;
        this.paddingY = renderResult.paddingY;
        this.offsetX = renderResult.offsetX;
        this.offsetY = renderResult.offsetY;
    }

    public SelectionState getCurrentState() { return currentState; }

    public void startDrag(SelectionState dragType) {
        this.currentState = dragType;
        double minSize = Math.min(tileWidth, tileHeight);
        dragGhost.setRadius(minSize * 0.35); // Scale ghost proportionally to minimum tile dimension
        dragGhost.setFill(dragType == SelectionState.DRAGGING_DEFENDER ? Color.rgb(0, 0, 255, 0.6) : Color.rgb(0, 255, 0, 0.6));
        dragGhost.setVisible(true);
    }

    public void updateDragPosition(double x, double y, GameMap gameMap) {
        if (currentState == SelectionState.NONE || currentState == SelectionState.SELECTING_DIRECTION) return;
        
        dragGhost.setCenterX(x);
        dragGhost.setCenterY(y);

        // Convert dynamic mouse position back to row/col grid coordinates
        int col = (int) Math.floor((x - offsetX) / (tileWidth + paddingX));
        int row = (int) Math.floor((y - offsetY) / (tileHeight + paddingY));

        clearRangePreview();

        // If hovering over a valid tile space, show temporary range preview
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

        int col = (int) Math.floor((x - offsetX) / (tileWidth + paddingX));
        int row = (int) Math.floor((y - offsetY) / (tileHeight + paddingY));

        if (row >= 0 && row < gameMap.getRows() && col >= 0 && col < gameMap.getCols()) {
            Tile tile = gameMap.getTile(row, col);
            
            if (!tile.isOccupied()) {
                if (currentState == SelectionState.DRAGGING_SNIPER && tile.canPlaceRanged()) {
                    pendingOperator = new Sniper(col, row);
                } else if (currentState == SelectionState.DRAGGING_DEFENDER && (tile.canPlaceMelee() || tile.isEnemyPath())) {
                    pendingOperator = new Defender(col, row);
                }

                if (pendingOperator != null) {
                    pendingOperator.setX(calcCenterX(col));
                    pendingOperator.setY(calcCenterY(row));
                    
                    pendingTile = tile;
                    pendingCol = col;
                    pendingRow = row;
                    currentState = SelectionState.SELECTING_DIRECTION;

                    // Compute true center pixel coordinates of the tile
                    double opCenterX = calcCenterX(col);
                    double opCenterY = calcCenterY(row);

                    // Initialize swipe tracking coords to unit tile's center
                    this.directionStartX = opCenterX;
                    this.directionStartY = opCenterY;

                    // Lock preliminary unit layout circle in place
                    double minDimension = Math.min(tileWidth, tileHeight);
                    double radius = minDimension * (pendingOperator.isGround() ? 0.32 : 0.28);
                    finalOpSprite = new Circle(radius);
                    finalOpSprite.setFill(pendingOperator.isGround() ? Color.BLUE : Color.GREEN);
                    finalOpSprite.setCenterX(opCenterX);
                    finalOpSprite.setCenterY(opCenterY);
                    root.getChildren().add(finalOpSprite);

                    // Add navigation direction arrow asset
                    finalDirectionArrow = new Polygon();
                    double arrowScale = minDimension / 62.0; // Scale arrow relative to tile dimension
                    finalDirectionArrow.getPoints().addAll(new Double[]{ 
                        0.0 * arrowScale, -8.0 * arrowScale, 
                        -6.0 * arrowScale,  4.0 * arrowScale, 
                         6.0 * arrowScale,  4.0 * arrowScale 
                    });
                    finalDirectionArrow.setFill(Color.GOLD);
                    finalDirectionArrow.setTranslateX(opCenterX);
                    finalDirectionArrow.setTranslateY(opCenterY);
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

    public void setDirectionDragStart(double x, double y) {
        this.directionStartX = x;
        this.directionStartY = y;
    }

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

        if (finalOpSprite != null) root.getChildren().remove(finalOpSprite);
        if (finalDirectionArrow != null) root.getChildren().remove(finalDirectionArrow);

        activeOperators.add(pendingOperator);

        // Create the real animated sprite only after deployment is confirmed.
        double spriteSize = Math.min(tileWidth, tileHeight) * 0.78;
        Color fallbackColor = pendingOperator.isGround() ? Color.BLUE : Color.GREEN;
        EntityAnimationController animation = new EntityAnimationController(
            pendingOperator,
            pendingOperator.getClass().getSimpleName(),
            false,
            spriteSize * 0.35,
            fallbackColor,
            spriteSize,
            spriteSize
        );
        animations.put(pendingOperator, animation);

        javafx.scene.Node operatorSprite = animation.getSprite().getNode();
        operatorSprite.layoutXProperty().bind(pendingOperator.xProperty().subtract(spriteSize / 2.0));
        operatorSprite.layoutYProperty().bind(pendingOperator.yProperty().subtract(spriteSize / 2.0));
        root.getChildren().add(operatorSprite);

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
            Rectangle rect = new Rectangle(tileWidth, tileHeight);
            rect.setFill(Color.rgb(255, 69, 0, 0.3));
            
            int c = (int) tilePos.getX();
            int r = (int) tilePos.getY();
            
            rect.setX(c * (tileWidth + paddingX) + offsetX);
            rect.setY(r * (tileHeight + paddingY) + offsetY);
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

    // Dynamic grid coordinate helper formulas
    private double calcCenterX(int col) {
        return offsetX + (col * (tileWidth + paddingX)) + (tileWidth / 2.0);
    }

    private double calcCenterY(int row) {
        return offsetY + (row * (tileHeight + paddingY)) + (tileHeight / 2.0);
    }

    public double getGameSpeedMultiplier() {
        if (currentState == SelectionState.DRAGGING_SNIPER || 
            currentState == SelectionState.DRAGGING_DEFENDER || 
            currentState == SelectionState.SELECTING_DIRECTION) {
            return 0;
        }
        return 1.0;
    }

    public void update(List<Enemy> activeEnemies) {
        for (Operator op : activeOperators) {
            op.update(activeEnemies);
            EntityAnimationController animation = animations.get(op);
            if (animation != null) {
                if (op.consumeAttackTriggered()) animation.triggerAttack();
                animation.update();
            }
        }
    }
}