package com.arclights;

import com.arclights.entity.enemy.EnemyType;
import com.arclights.handlers.InputController;
import com.arclights.managers.DeploymentManager;
import com.arclights.managers.EnemyManager;
import com.arclights.models.GameMap;
import com.arclights.models.MapPresets;
import com.arclights.models.Tile;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        Pane root = new Pane();

        GameMap map1 = new GameMap(MapPresets.LEVEL_1);
        GameMap map2 = new GameMap(MapPresets.LEVEL_2);

        GameMap gameMap = map1;

        EnemyManager enemyManager = new EnemyManager(root, gameMap);
        DeploymentManager deploymentManager = new DeploymentManager(root);

        int tileSize = 60;
        int padding = 2;

        // Render Map Grid
        for (int row = 0; row < gameMap.getRows(); row++) {
            for (int col = 0; col < gameMap.getCols(); col++) {
                Rectangle tileNode = new Rectangle(tileSize, tileSize);
                Tile logicTile = gameMap.getTile(row, col);

                if (logicTile.getTileType() == Tile.TileType.RANGED_HIGH_GROUND) {
                    tileNode.setFill(Color.DARKGRAY);
                } else if (logicTile.getTileType() == Tile.TileType.ENEMY_SPAWN) {
                    tileNode.setFill(Color.RED);
                } else if (logicTile.getTileType() == Tile.TileType.PLAYER_OBJECTIVE) {
                    tileNode.setFill(Color.BLUE);
                } else if (logicTile.getDeploymentType() == Tile.DeploymentType.MELEE_ONLY) {
                    tileNode.setFill(Color.LIGHTGRAY);
                } else {
                    tileNode.setFill(Color.LIGHTPINK);
                }

                tileNode.setX(col * (tileSize + padding) + 50);
                tileNode.setY(row * (tileSize + padding) + 50);
                root.getChildren().add(tileNode);
            }
        }

        // UI Header Text (Updated instructions to reflect double-drag mechanic)
        Label statusLabel = new Label(
                "Instructions: Drag card to map & release. Then drag & release on unit to set range & deploy!");
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");

        // Draggable Character Cards (Represented visually as styled UI boxes)
        Rectangle sniperCard = new Rectangle(130, 50, Color.GREEN);
        Label sniperLabel = new Label(" SNIPER ");
        sniperLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        Pane sniperGroup = new Pane(sniperCard, sniperLabel);
        sniperLabel.setLayoutY(15);
        sniperLabel.setLayoutX(35);

        Rectangle defenderCard = new Rectangle(130, 50, Color.BLUE);
        Label defenderLabel = new Label(" DEFENDER ");
        defenderLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        Pane defenderGroup = new Pane(defenderCard, defenderLabel);
        defenderLabel.setLayoutY(15);
        defenderLabel.setLayoutX(25);

        HBox cardDeckDeck = new HBox(20, sniperGroup, defenderGroup);
        // ... [Keep your layout and rendering code up to line 66] ...

        VBox controlDashboard = new VBox(10, statusLabel, cardDeckDeck);
        controlDashboard.setStyle("-fx-background-color: #222222; -fx-padding: 15px; -fx-background-radius: 5px;");
        controlDashboard.setLayoutX(50);
        controlDashboard.setLayoutY(460);
        root.getChildren().add(controlDashboard);

        // --- New Refactored Input Handling Call ---
        InputController inputController = new InputController(deploymentManager, gameMap);
        inputController.attachInputHandlers(root, sniperGroup, defenderGroup);

        // Run engine clock configurations
        enemyManager.spawnEnemy(EnemyType.BOSS);

        AnimationTimer gameLoop = new AnimationTimer() {
            private long lastTime = 0;
            private double accumulatedTime = 0;

            // 16.67 milliseconds is roughly 1 frame at 60 FPS (in nanoseconds)
            private final double TARGET_FRAME_TIME = 16_666_666.0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }

                // Calculate actual time passed since last frame
                long elapsedNano = now - lastTime;
                lastTime = now;

                // Multiply the elapsed real time by our game speed factor (1.0 or 0.1)
                double speedMultiplier = deploymentManager.getGameSpeedMultiplier();
                accumulatedTime += elapsedNano * speedMultiplier;

                // Only update the game world physics/logic when enough "game time" has passed
                while (accumulatedTime >= TARGET_FRAME_TIME) {
                    enemyManager.update();
                    deploymentManager.update(enemyManager.getActiveEnemies());

                    accumulatedTime -= TARGET_FRAME_TIME;
                }
            }
        };
        gameLoop.start();

        Scene scene = new Scene(root, 800, 650);
        stage.setTitle("Arclights - Gesture Deployment Clone");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}