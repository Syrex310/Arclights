package com.arclights;

import com.arclights.managers.DeploymentManager;
import com.arclights.managers.EnemyManager;
import com.arclights.models.GameMap;
import com.arclights.models.Tile;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();
        GameMap gameMap = new GameMap();

        // Initialize our isolated logic and object lifecycle managers
        EnemyManager enemyManager = new EnemyManager(root);
        DeploymentManager deploymentManager = new DeploymentManager(root);

        int tileSize = 60;
        int padding = 2;

        // Render the background grid layout
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

                // Click handler simplified via Manager delegation
                final int finalRow = row;
                final int finalCol = col;
                tileNode.setOnMouseClicked(event -> {
                    deploymentManager.tryDeploy(logicTile, finalCol, finalRow);
                });
            }
        }

        // Spawn operational units
        enemyManager.spawnEnemy();

        // Streamlined Core Tick Engine Loop
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // I. Update active enemy logic states
                enemyManager.update();

                // II. Pass tracked enemies registry downwards to run range scanner updates
                deploymentManager.update(enemyManager.getActiveEnemies());
                
                // Note: Circle geometry rendering is handled automatically now via property tracking bindings!
            }
        };
        gameLoop.start();

        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Arclights - Combat Demonstration Alpha");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) { launch(); }
}