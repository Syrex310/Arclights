package com.arclights.managers;

import java.util.ArrayList;
import java.util.List;

import com.arclights.models.Defender;
import com.arclights.models.Enemy;
import com.arclights.models.Operator;
import com.arclights.models.Sniper;
import com.arclights.models.Tile;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class DeploymentManager {
    private final List<Operator> activeOperators = new ArrayList<>();
    private final Pane root;

    public DeploymentManager(Pane root) {
        this.root = root;
    }

    public void tryDeploy(Tile tile, int col, int row) {
        if (tile.isOccupied()) return;

        Operator newOp = null;
        Circle opSprite = null;

        if (tile.canPlaceRanged()) {
            tile.setOccupied(true);
            newOp = new Sniper(col, row); // (col, row) -> (X, Y)
            opSprite = new Circle(18, Color.GREEN);
            System.out.println("Deployed Sniper at Column: " + col + ", Row: " + row);
        } 
        else if (tile.canPlaceMelee() || tile.isEnemyPath()) {
            tile.setOccupied(true);
            newOp = new Defender(col, row); // FIX: Swapped from (row, col) to correct (col, row) placement configuration!
            opSprite = new Circle(20, Color.BLUE);
            System.out.println("Deployed Defender at Column: " + col + ", Row: " + row);
        }

        if (newOp != null) {
            activeOperators.add(newOp);
            
            // Set position based on the underlying calculated properties of the engine layout
            opSprite.setCenterX(newOp.getX());
            opSprite.setCenterY(newOp.getY());
            root.getChildren().add(opSprite);
        }
    }

    public void update(List<Enemy> activeEnemies) {
        for (Operator op : activeOperators) {
            op.update(activeEnemies);
        }
    }
}