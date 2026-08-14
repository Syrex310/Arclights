package com.arclights.managers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arclights.animation.EntityAnimationController;
import com.arclights.animation.SpriteSizing;
import com.arclights.entity.enemy.Enemy;
import com.arclights.entity.operator.Operator;
import com.arclights.models.GameMap;
import com.arclights.models.OperatorCatalog;
import com.arclights.models.Tile;
import com.arclights.ui.EntityLayer;
import com.arclights.ui.MapRenderer;
import com.arclights.ui.OperatorSkillPanel;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public class DeploymentManager {
    public enum SelectionState { NONE, DRAGGING, SELECTING_DIRECTION }

    // Deployment Points (DP): starting pool, hard cap, and passive regen rate.
    // 1 DP is regenerated per second of game time. Since DeploymentManager.update()
    // is called once per simulated 1/60s tick (see App's game loop), each tick
    // regenerates 1/60 DP.
    private static final double STARTING_DP = 20.0;
    private static final double MAX_DP = 99.0;
    private static final double DP_PER_TICK = 1.0 / 60.0;

    private double currentDP = STARTING_DP;

    private final List<Operator> activeOperators = new ArrayList<>();
    private final Map<Operator, EntityAnimationController> animations = new HashMap<>();
    private final List<Rectangle> rangePreviewNodes = new ArrayList<>();
    private final Pane root;
    private final EntityLayer entityLayer;
    private final OperatorSkillPanel skillPanel;
    private final Map<Operator, Tile> operatorTiles = new HashMap<>();

    // Dynamic map alignment layout metrics
    private double tileWidth;
    private double tileHeight;
    private double paddingX;
    private double paddingY;
    private double offsetX;
    private double offsetY;

    private SelectionState currentState = SelectionState.NONE;
    private String draggingOperatorId;
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

    public DeploymentManager(Pane root, EntityLayer entityLayer) {
        this.root = root;
        this.entityLayer = entityLayer;
        this.skillPanel = new OperatorSkillPanel();
        this.root.getChildren().add(skillPanel.getRoot());
        
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

    public int getCurrentDP() {
        return (int) Math.floor(currentDP);
    }

    public int getMaxDP() {
        return (int) MAX_DP;
    }

    /** Whether the given tile allows the operator type described by this catalog definition to be placed on it. */
    private boolean canPlaceOn(Tile tile, OperatorCatalog.Definition def) {
        if (tile.isOccupied() || def == null) return false;
        return def.placement == OperatorCatalog.Placement.RANGED
            ? tile.canPlaceRanged()
            : (tile.canPlaceMelee() || tile.isEnemyPath());
    }

    /** Whether enough DP is currently banked to deploy the given owned operator (by catalog id). */
    public boolean canAfford(String operatorId) {
        OperatorCatalog.Definition def = OperatorCatalog.get(operatorId);
        return def != null && currentDP >= def.deployCost;
    }

    public void startDrag(String operatorId) {
        OperatorCatalog.Definition def = OperatorCatalog.get(operatorId);
        if (def == null || !canAfford(operatorId)) return;

        this.draggingOperatorId = operatorId;
        this.currentState = SelectionState.DRAGGING;
        double minSize = Math.min(tileWidth, tileHeight);
        dragGhost.setRadius(minSize * 0.35); // Scale ghost proportionally to minimum tile dimension
        dragGhost.setFill(Color.color(
            def.cardColor.getRed(), def.cardColor.getGreen(), def.cardColor.getBlue(), 0.6));
        dragGhost.setVisible(true);
    }

    public void updateDragPosition(double x, double y, GameMap gameMap) {
        if (currentState != SelectionState.DRAGGING) return;

        dragGhost.setCenterX(x);
        dragGhost.setCenterY(y);

        // Convert dynamic mouse position back to row/col grid coordinates
        int col = (int) Math.floor((x - offsetX) / (tileWidth + paddingX));
        int row = (int) Math.floor((y - offsetY) / (tileHeight + paddingY));

        clearRangePreview();

        OperatorCatalog.Definition def = OperatorCatalog.get(draggingOperatorId);

        // If hovering over a valid tile space, show temporary range preview
        if (def != null && row >= 0 && row < gameMap.getRows() && col >= 0 && col < gameMap.getCols()) {
            Tile tile = gameMap.getTile(row, col);
            if (canPlaceOn(tile, def)) {
                showRangePreview(def.create(col, row));
            }
        }
    }

    public void handleRelease(double x, double y, GameMap gameMap) {
        dragGhost.setVisible(false);
        if (currentState != SelectionState.DRAGGING) return;

        int col = (int) Math.floor((x - offsetX) / (tileWidth + paddingX));
        int row = (int) Math.floor((y - offsetY) / (tileHeight + paddingY));

        OperatorCatalog.Definition def = OperatorCatalog.get(draggingOperatorId);

        if (def != null && row >= 0 && row < gameMap.getRows() && col >= 0 && col < gameMap.getCols()) {
            Tile tile = gameMap.getTile(row, col);

            if (canPlaceOn(tile, def)) {
                pendingOperator = def.create(col, row);

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

        // Guard against DP being spent elsewhere mid-drag; deployment cannot
        // proceed without enough banked DP to cover this operator's cost.
        if (currentDP < pendingOperator.getDeployCost()) {
            cancelPendingDeployment();
            return;
        }
        currentDP -= pendingOperator.getDeployCost();

        pendingTile.setOccupied(true);
        operatorTiles.put(pendingOperator, pendingTile);

        if (finalOpSprite != null) root.getChildren().remove(finalOpSprite);
        if (finalDirectionArrow != null) root.getChildren().remove(finalDirectionArrow);

        activeOperators.add(pendingOperator);

        // Create the real animated sprite only after deployment is confirmed.
        double spriteSize = SpriteSizing.operatorSize(tileWidth, tileHeight);
        Color fallbackColor = pendingOperator.isGround() ? Color.BLUE : Color.GREEN;
        EntityAnimationController animation = new EntityAnimationController(
            pendingOperator,
            pendingOperator.getClass().getSimpleName(),
            false,
            18,
            fallbackColor,
            spriteSize,
            spriteSize
        );
        animations.put(pendingOperator, animation);
        animation.triggerStart();

        javafx.scene.Node operatorSprite = animation.getSprite().getNode();
        final Operator deployedOperator = pendingOperator;
        operatorSprite.setOnMouseClicked(event -> {
            skillPanel.selectOperator(deployedOperator);
            event.consume();
        });
        operatorSprite.layoutXProperty().bind(pendingOperator.xProperty().subtract(spriteSize * 0.435));
        operatorSprite.layoutYProperty().bind(pendingOperator.yProperty().subtract(spriteSize * 0.75));
        entityLayer.track(operatorSprite, pendingOperator::getY);

        currentState = SelectionState.NONE;
        draggingOperatorId = null;
        pendingOperator = null;
        pendingTile = null;
        finalOpSprite = null;
        finalDirectionArrow = null;
        clearRangePreview();
        System.out.println("Deployment bound locked successfully.");
    }

    /** Aborts a pending placement (e.g. insufficient DP) and clears its preview visuals. */
    private void cancelPendingDeployment() {
        if (pendingTile != null) {
            pendingTile.setOccupied(false);
        }
        if (finalOpSprite != null) root.getChildren().remove(finalOpSprite);
        if (finalDirectionArrow != null) root.getChildren().remove(finalDirectionArrow);

        currentState = SelectionState.NONE;
        draggingOperatorId = null;
        pendingOperator = null;
        pendingTile = null;
        finalOpSprite = null;
        finalDirectionArrow = null;
        clearRangePreview();
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
        if (currentState == SelectionState.DRAGGING ||
            currentState == SelectionState.SELECTING_DIRECTION) {
            return 0.1;
        }
        return 1.0;
    }

    public void update(List<Enemy> activeEnemies) {
        currentDP = Math.min(MAX_DP, currentDP + DP_PER_TICK);

        for (Operator op : activeOperators) {
            op.update(activeEnemies);
            EntityAnimationController animation = animations.get(op);
            if (animation != null) {
                if (op.consumeAttackTriggered()) animation.triggerAttack();
                animation.update();
            }
        }
        skillPanel.refresh();

        var iterator = activeOperators.iterator();

        while (iterator.hasNext()) {
            Operator op = iterator.next();
            EntityAnimationController animation = animations.get(op);

            if (!op.isAlive()
                    && (animation == null
                        || animation.getSprite().isCurrentAnimationFinished())) {

                if (animation != null) {
                    entityLayer.untrack(animation.getSprite().getNode());
                }

                Tile tile = operatorTiles.remove(op);
                if (tile != null) {
                    tile.setOccupied(false);
                }

                animations.remove(op);
                iterator.remove();
            }
        }
    }
}