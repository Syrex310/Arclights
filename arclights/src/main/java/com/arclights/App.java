package com.arclights;

import com.arclights.handlers.InputController;
import com.arclights.managers.DeploymentManager;
import com.arclights.managers.EnemyManager;
import com.arclights.models.GameMap;
import com.arclights.models.MapConfig;
import com.arclights.models.MapPresets;
import com.arclights.models.PlayerProgress;
import com.arclights.models.wave.StageWaveConfigs;
import com.arclights.models.wave.WaveConfig;
import com.arclights.ui.EntityLayer;
import com.arclights.ui.LoadingScreen;
import com.arclights.ui.MapRenderer;
import com.arclights.ui.OperatorDeploymentBar;
import com.arclights.ui.OperatorListView;
import com.arclights.ui.ShopMenu;
import com.arclights.ui.StagePreview;
import com.arclights.ui.StartMenu;
import com.arclights.ui.UILoader;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
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
        showLoadingScreen(stage);
    }

    /**
     * Shown first, before the player can touch anything: preloads every
     * operator/enemy sprite animation for all three playable maps (see
     * AssetPreloader) so the very first battle a player enters doesn't stall
     * decoding PNGs on the FX thread.
     */
    private void showLoadingScreen(Stage stage) {
        stage.setScene(LoadingScreen.createScene(() -> showStartMenu(stage)));
        stage.setTitle("Arclights - Loading");
        stage.show();
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
            public void onShopClick() {
                showShopScreen(stage);
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

    private void showShopScreen(Stage stage) {
        stage.setScene(ShopMenu.createScene(() -> showStartMenu(stage), () -> showOperatorScreen(stage)));
    }

    private void startGame(Stage stage, char[][] levelLayout, String levelName) {
        Pane root = new Pane();
        root.setStyle("-fx-background-color: #121212;");

        MapConfig mapConfig = MapPresets.getConfigForLayout(levelLayout);
        GameMap gameMap = new GameMap(levelLayout, mapConfig);

        MapRenderer.RenderResult layout = MapRenderer.renderMap(root, gameMap, UILoader.WINDOW_WIDTH, UILoader.WINDOW_HEIGHT); // 1280 x 645

        // Dedicated, depth-sorted layer for operator/enemy sprites, sitting
        // right above the map tiles so entities lower on the map (larger Y)
        // are drawn in front of entities higher up.
        EntityLayer entityLayer = new EntityLayer();
        root.getChildren().add(entityLayer.getPane());

        DeploymentManager deploymentManager = new DeploymentManager(root, entityLayer);
        deploymentManager.updateMapLayout(layout);

        EnemyManager enemyManager = new EnemyManager(entityLayer, gameMap, layout);
        enemyManager.setOnGameOver(() -> {
            if (gameLoop != null) {
                gameLoop.stop();
            }
            showDefeatOverlay(stage, root);
        });
        enemyManager.setOnStageClear(() -> {
            if (gameLoop != null) {
                gameLoop.stop();
            }
            int reward = grantStageClearReward(levelLayout, levelName);
            showVictoryOverlay(stage, root, reward);
        });

        Label statusLabel = new Label(
                "Level: " + levelName + " | Drag & release to deploy");
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
        //root.getChildren().add(controlDashboard);

        OperatorDeploymentBar deploymentBar = new OperatorDeploymentBar(levelName);

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

        root.getChildren().addAll(deploymentBar.getRoot(), exitBtn);

        InputController inputController = new InputController(deploymentManager, gameMap);
        inputController.attachInputHandlers(
            root,
            deploymentBar.getOperatorCards()
        );

        WaveConfig waveConfig = StageWaveConfigs.getConfigForLayout(levelLayout);
        enemyManager.loadWaveConfig(waveConfig);

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
                    deploymentBar.updateDeploymentPoints(deploymentManager.getCurrentDP(), deploymentManager.getMaxDP());
                    entityLayer.sortByDepth();
                    accumulatedTime -= TARGET_FRAME_TIME;
                }
            }
        };
        gameLoop.start();

        Scene scene = new Scene(root, UILoader.WINDOW_WIDTH, UILoader.WINDOW_HEIGHT);
        stage.setScene(scene);
    }

    /**
     * Full-screen defeat overlay shown once the base runs out of lives.
     * Blocks further interaction with the field (it sits on top and
     * captures clicks) and returns to stage select on click anywhere.
     */
    private void showDefeatOverlay(Stage stage, Pane root) {
        Pane overlay = new Pane();
        overlay.setPrefSize(UILoader.WINDOW_WIDTH, UILoader.WINDOW_HEIGHT);
        overlay.setMinSize(UILoader.WINDOW_WIDTH, UILoader.WINDOW_HEIGHT);
        overlay.setStyle("-fx-background-color: rgba(10, 0, 0, 0.82); -fx-cursor: hand;");

        Label defeatLabel = new Label("DEFEAT");
        defeatLabel.setStyle(
            "-fx-text-fill: #dc3545; " +
            "-fx-font-size: 64px; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-font-weight: 900;"
        );

        Label subLabel = new Label("The objective was overrun. Click anywhere to return to base.");
        subLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 15px;");

        VBox messageBox = new VBox(15, defeatLabel, subLabel);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPrefWidth(UILoader.WINDOW_WIDTH);
        messageBox.setLayoutY((UILoader.WINDOW_HEIGHT / 2.0) - 60);

        overlay.getChildren().add(messageBox);
        overlay.setOnMouseClicked(e -> showStageSelect(stage));

        root.getChildren().add(overlay);
    }

    /**
     * Reward rules: stages 1-1 and 1-2 grant 1000 crystals on their very
     * first clear, 500 on every clear after that. Stage 1-3 is planned as
     * an infinite-wave map and has no reward yet, so it's excluded here on
     * purpose - extend this once that reward is designed.
     *
     * @return the crystal amount actually granted (0 if this stage isn't reward-eligible yet)
     */
    private int grantStageClearReward(char[][] levelLayout, String levelName) {
        boolean rewardEligible = levelLayout == MapPresets.LEVEL_1_LAYOUT
                || levelLayout == MapPresets.LEVEL_2_LAYOUT;

        if (!rewardEligible) {
            return 0;
        }

        boolean firstClear = PlayerProgress.isFirstClear(levelName);
        int reward = firstClear ? 1000 : 500;

        PlayerProgress.markCleared(levelName);
        PlayerProgress.addCrystals(reward);

        return reward;
    }

    /**
     * Full-screen victory overlay shown once every scheduled wave has been
     * cleared and no enemies remain. Blocks further interaction with the
     * field and returns to stage select on click anywhere.
     */
    private void showVictoryOverlay(Stage stage, Pane root, int crystalsAwarded) {
        Pane overlay = new Pane();
        overlay.setPrefSize(UILoader.WINDOW_WIDTH, UILoader.WINDOW_HEIGHT);
        overlay.setMinSize(UILoader.WINDOW_WIDTH, UILoader.WINDOW_HEIGHT);
        overlay.setStyle("-fx-background-color: rgba(0, 10, 5, 0.82); -fx-cursor: hand;");

        Label victoryLabel = new Label("OPERATION COMPLETE");
        victoryLabel.setStyle(
            "-fx-text-fill: #2ecc71; " +
            "-fx-font-size: 52px; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-font-weight: 900;"
        );

        String rewardText = crystalsAwarded > 0
                ? "+" + crystalsAwarded + " crystals"
                : "No crystal reward for this stage yet";
        Label rewardLabel = new Label(rewardText);
        rewardLabel.setStyle("-fx-text-fill: #ffd54f; -fx-font-size: 20px; -fx-font-weight: bold;");

        Label subLabel = new Label("Click anywhere to return to base.");
        subLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 15px;");

        VBox messageBox = new VBox(12, victoryLabel, rewardLabel, subLabel);
        messageBox.setAlignment(Pos.CENTER);
        messageBox.setPrefWidth(UILoader.WINDOW_WIDTH);
        messageBox.setLayoutY((UILoader.WINDOW_HEIGHT / 2.0) - 70);

        overlay.getChildren().add(messageBox);
        overlay.setOnMouseClicked(e -> showStageSelect(stage));

        root.getChildren().add(overlay);
    }

    public static void main(String[] args) {
        launch();
    }
}