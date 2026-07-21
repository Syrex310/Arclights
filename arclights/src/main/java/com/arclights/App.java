package com.arclights;

import com.arclights.entity.enemy.EnemyType;
import com.arclights.handlers.InputController;
import com.arclights.managers.DeploymentManager;
import com.arclights.managers.EnemyManager;
import com.arclights.models.GameMap;
import com.arclights.models.Tile;
import com.arclights.ui.OperatorListView;
import com.arclights.ui.StagePreview;
import com.arclights.ui.StartMenu;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class App extends Application {

    private AnimationTimer gameLoop;

    @Override
    public void start(Stage stage) {
        showStartMenu(stage);
    }

    private void showStartMenu(Stage stage) {
        stage.setScene(StartMenu.createScene(new StartMenu.MenuCallbacks() {
            @Override
            public void onTerminalClick() {
                showStageSelect(stage);
            }

            @Override
            public void onOperatorsClick() {
                showOperatorScreen(stage);
            }

            @Override
            public void onExitClick() {
                stage.close();
                System.exit(0);
            }
        }));
        stage.setTitle("Arclights - Main Terminal");
        stage.show();
    }

    private void showStageSelect(Stage stage) {
        stage.setScene(StagePreview.createScene(new StagePreview.StageSelectCallbacks() {
            @Override
            public void onBackToMenu() {
                showStartMenu(stage);
            }

            @Override
            public void onDeployStage(char[][] layout, String name) {
                startGame(stage, layout, name);
            }
        }));
    }

    private void showOperatorScreen(Stage stage) {
        stage.setScene(OperatorListView.createScene(() -> showStartMenu(stage)));
    }

    private void startGame(Stage stage, char[][] levelLayout, String levelName) {
        Pane root = new Pane();
        root.setStyle("-fx-background-color: #121212;");

        GameMap gameMap = new GameMap(levelLayout);

        EnemyManager enemyManager = new EnemyManager(root, gameMap);
        DeploymentManager deploymentManager = new DeploymentManager(root);

        int tileSize = 60;
        int padding = 2;

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

        Label statusLabel = new Label(
                "Level: " + levelName + " | Drag card to map & release. Then drag & release on unit to set range & deploy!");
        statusLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 13px;");

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

        VBox controlDashboard = new VBox(10, statusLabel, cardDeckDeck);
        controlDashboard.setStyle("-fx-background-color: #222222; -fx-padding: 15px; -fx-background-radius: 5px;");
        controlDashboard.setLayoutX(50);
        controlDashboard.setLayoutY(460);
        root.getChildren().add(controlDashboard);

        Button exitBtn = new Button("QUIT OPERATION");
        exitBtn.setStyle(
            "-fx-background-color: rgba(220, 53, 69, 0.15); " +
            "-fx-text-fill: #dc3545; " +
            "-fx-border-color: #dc3545; " +
            "-fx-border-width: 1px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px; " +
            "-fx-cursor: hand;"
        );
        exitBtn.setPrefSize(140, 35);
        exitBtn.setLayoutX(600);
        exitBtn.setLayoutY(50);
        exitBtn.setOnMouseEntered(e -> exitBtn.setStyle(
            "-fx-background-color: #dc3545; " +
            "-fx-text-fill: #ffffff; " +
            "-fx-border-color: #ffffff; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px; " +
            "-fx-cursor: hand;"
        ));
        exitBtn.setOnMouseExited(e -> exitBtn.setStyle(
            "-fx-background-color: rgba(220, 53, 69, 0.15); " +
            "-fx-text-fill: #dc3545; " +
            "-fx-border-color: #dc3545; " +
            "-fx-border-width: 1px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px; " +
            "-fx-cursor: hand;"
        ));
        exitBtn.setOnAction(e -> {
            if (gameLoop != null) {
                gameLoop.stop();
            }
            showStageSelect(stage);
        });
        root.getChildren().add(exitBtn);

        InputController inputController = new InputController(deploymentManager, gameMap);
        inputController.attachInputHandlers(root, sniperGroup, defenderGroup);

        enemyManager.spawnEnemy(EnemyType.BOSS);

        gameLoop = new AnimationTimer() {
            private long lastTime = 0;
            private double accumulatedTime = 0;
            private final double TARGET_FRAME_TIME = 16_666_666.0;

            @Override
            public void handle(long now) {
                if (lastTime == 0) {
                    lastTime = now;
                    return;
                }

                long elapsedNano = now - lastTime;
                lastTime = now;

                double speedMultiplier = deploymentManager.getGameSpeedMultiplier();
                accumulatedTime += elapsedNano * speedMultiplier;

                while (accumulatedTime >= TARGET_FRAME_TIME) {
                    enemyManager.update();
                    deploymentManager.update(enemyManager.getActiveEnemies());
                    accumulatedTime -= TARGET_FRAME_TIME;
                }
            }
        };
        gameLoop.start();

        Scene scene = new Scene(root, 800, 650);
        stage.setScene(scene);
    }

    public static void main(String[] args) {
        launch();
    }
}