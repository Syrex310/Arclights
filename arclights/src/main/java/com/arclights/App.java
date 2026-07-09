package com.arclights;

import com.arclights.models.Enemy; // Make sure your imports match your folder names!
import com.arclights.models.GameMap;
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

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();
        GameMap gameMap = new GameMap();

        int tileSize = 60;
        int padding = 2;

        // 1. Draw the static map background
        for (int row = 0; row < gameMap.getRows(); row++) {
            for (int col = 0; col < gameMap.getCols(); col++) {
                Rectangle tileNode = new Rectangle(tileSize, tileSize);
                Tile logicTile = gameMap.getTile(row, col);
                
                if (logicTile.getType() == 0) {
                    tileNode.setFill(Color.LIGHTGRAY);
                } else if (logicTile.getType() == 1) {
                    tileNode.setFill(Color.DARKGRAY);
                } else if (logicTile.isEnemyPath()) {
                    tileNode.setFill(Color.LIGHTPINK);
                }

                tileNode.setX(col * (tileSize + padding) + 50);
                tileNode.setY(row * (tileSize + padding) + 50);
                root.getChildren().add(tileNode);
                final int finalRow = row;
                final int finalCol = col;
                
                tileNode.setOnMouseClicked(event -> {
                    // Check if it's high ground and not occupied
                    if (logicTile.getType() == 1 && !logicTile.isOccupied()) {
                        
                        // 1. Mark the logic tile as occupied so we can't double-deploy
                        logicTile.setOccupied(true);
                        System.out.println("Deployed Sniper at: " + finalCol + ", " + finalRow);

                        // 2. Create the visual representation (a Green Circle for the Sniper)
                        Circle sniperSprite = new Circle(18, Color.GREEN);
                        sniperSprite.setCenterX(calcX(finalCol));
                        sniperSprite.setCenterY(calcY(finalRow));
                        
                        // Add it to the screen instantly
                        root.getChildren().add(sniperSprite);
                        
                        // Optional: Add the sniper object to an active units tracking array later!
                    }
                });
            }
        }

        // 2. Define the path checkpoints based on (Col, Row) coordinates
        java.util.List<javafx.geometry.Point2D> path = new java.util.ArrayList<>();
        
        // Helper lambda/method to calculate center of any given tile index
        int startCol = 0, startRow = 1;

        // Add checkpoints matching our pink tiles layout turns
        path.add(new javafx.geometry.Point2D(calcX(3), calcY(1))); // Corner 1
        path.add(new javafx.geometry.Point2D(calcX(3), calcY(3))); // Corner 2
        path.add(new javafx.geometry.Point2D(calcX(7), calcY(3))); // Final Goal

        // Spawn enemy at the very beginning point (0, 1)
        Enemy originiumSlug = new Enemy(calcX(0), calcY(1), 100, 10, 1.5, path);

        // 3. Create the visual representation (a red circle)
        Circle enemySprite = new Circle(15, Color.RED);
        enemySprite.setCenterX(originiumSlug.getX());
        enemySprite.setCenterY(originiumSlug.getY());
        root.getChildren().add(enemySprite);

        // 4. THE MINECRAFT-STYLE TICK LOOP
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // I. Update Engine Logic (The invisible data changes)
                originiumSlug.update(); 

                // II. Update Visuals (Sync the JavaFX shape to match the data model coordinates)
                enemySprite.setCenterX(originiumSlug.getX());
                enemySprite.setCenterY(originiumSlug.getY());
            }
        };
        
        // Start the heartbeat!
        gameLoop.start();

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Arclights - The Heartbeat Loop");
        stage.setScene(scene);
        stage.show();
    }
    double calcX(int col) { return col * 62 + 50 + 30; }
    double calcY(int row) { return row * 62 + 50 + 30; }
    public static void main(String[] args) {
        launch();
    }
}