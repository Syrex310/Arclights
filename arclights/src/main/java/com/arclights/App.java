package com.arclights;

import java.util.ArrayList;
import java.util.List;

import com.arclights.models.Enemy;
import com.arclights.models.GameMap;
import com.arclights.models.Operator;
import com.arclights.models.Sniper;
import com.arclights.models.Tile;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class App extends Application {

    // Global active engine entity arrays
    private final List<Enemy> activeEnemies = new ArrayList<>();
    private final List<Operator> activeOperators = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();
        GameMap gameMap = new GameMap();

        int tileSize = 60;
        int padding = 2;

        // 1. Render the background grid layout
        for (int row = 0; row < gameMap.getRows(); row++) {
            for (int col = 0; col < gameMap.getCols(); col++) {
                Rectangle tileNode = new Rectangle(tileSize, tileSize);
                Tile logicTile = gameMap.getTile(row, col);
                
                if (logicTile.getType() == 0) tileNode.setFill(Color.LIGHTGRAY);
                else if (logicTile.getType() == 1) tileNode.setFill(Color.DARKGRAY);
                else if (logicTile.isEnemyPath()) tileNode.setFill(Color.LIGHTPINK);

                tileNode.setX(col * (tileSize + padding) + 50);
                tileNode.setY(row * (tileSize + padding) + 50);
                root.getChildren().add(tileNode);

                // Click handler to deploy units
                final int finalRow = row;
                final int finalCol = col;
                tileNode.setOnMouseClicked(event -> {
                    if (logicTile.getType() == 1 && !logicTile.isOccupied()) {
                        logicTile.setOccupied(true);
                        
                        // Spawn logic model and log to tracker array
                        Sniper newSniper = new Sniper(finalCol, finalRow);
                        activeOperators.add(newSniper);

                        // Spawn graphics node representation
                        Circle sniperSprite = new Circle(18, Color.GREEN);
                        sniperSprite.setCenterX(calcX(finalCol));
                        sniperSprite.setCenterY(calcY(finalRow));
                        root.getChildren().add(sniperSprite);
                        
                        System.out.println("Deployed Sniper at Column: " + finalCol + ", Row: " + finalRow);
                    }
                });
            }
        }

        // 2. Set up enemy paths and waypoints
        List<javafx.geometry.Point2D> path = new ArrayList<>();
        path.add(new javafx.geometry.Point2D(calcX(3), calcY(1)));
        path.add(new javafx.geometry.Point2D(calcX(3), calcY(3)));
        path.add(new javafx.geometry.Point2D(calcX(7), calcY(3)));

        // Instantiate enemy slug and add it to our tracking collection list
        Enemy originiumSlug = new Enemy(calcX(0), calcY(1), 100, 10, 1.0, path);
        activeEnemies.add(originiumSlug);

        // Visual circle node representing the enemy
        Circle enemySprite = new Circle(15, Color.RED);
        enemySprite.setCenterX(originiumSlug.getX());
        enemySprite.setCenterY(originiumSlug.getY());
        root.getChildren().add(enemySprite);

        // 3. MINECRAFT-STYLE ENGINE TICK TICK LOOP
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // I. Update Active Enemies Position
                for (Enemy enemy : activeEnemies) {
                    enemy.update();
                }

                // II. Update Active Operators Tracking Targets
                for (Operator op : activeOperators) {
                    op.update(activeEnemies); // Run scan updates against enemy registry lists
                }

                // III. Update Canvas Graphics Objects
                if (originiumSlug.isAlive()) {
                    enemySprite.setCenterX(originiumSlug.getX());
                    enemySprite.setCenterY(originiumSlug.getY());
                } else {
                    root.getChildren().remove(enemySprite); // Despawn visual asset if dead
                }
            }
        };
        gameLoop.start();

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Arclights - Combat Demonstration Alpha");
        stage.setScene(scene);
        stage.show();
    }

    private double calcX(int col) { return col * 62 + 50 + 30; }
    private double calcY(int row) { return row * 62 + 50 + 30; }

    public static void main(String[] args) { launch(); }
}