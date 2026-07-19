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
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.io.InputStream;

public class App extends Application {

    private AnimationTimer gameLoop;

    @Override
    public void start(Stage stage) {
        showStartMenu(stage);
    }

    private void showStartMenu(Stage stage) {
        Pane root = new Pane();
        root.setPrefSize(800, 650);

        // Load background image
        ImageView bgView = null;
        try {
            InputStream bgStream = getClass().getResourceAsStream("background.png");
            if (bgStream != null) {
                Image bgImage = new Image(bgStream);
                bgView = new ImageView(bgImage);
                bgView.setFitWidth(800);
                bgView.setFitHeight(650);
                bgView.setPreserveRatio(false);
            }
        } catch (Exception e) {
            System.err.println("Could not load background image: " + e.getMessage());
        }

        if (bgView != null) {
            root.getChildren().add(bgView);
        } else {
            // Fallback dark gradient
            Rectangle fallbackBg = new Rectangle(800, 650);
            fallbackBg.setFill(new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#0d0f12")),
                new Stop(1, Color.web("#1c2026"))
            ));
            root.getChildren().add(fallbackBg);
        }

        // Overlay dark tint for better contrast
        Rectangle tint = new Rectangle(800, 650);
        tint.setFill(Color.rgb(10, 12, 15, 0.4));
        root.getChildren().add(tint);

        // Left Panel - Logo & Status Info
        VBox infoPanel = new VBox(15);
        infoPanel.setLayoutX(60);
        infoPanel.setLayoutY(160);
        infoPanel.setPrefWidth(350);

        Label titleLabel = new Label("A R C L I G H T S");
        titleLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 42px; -fx-font-family: 'Arial'; -fx-font-weight: 900;");
        
        DropShadow ds = new DropShadow();
        ds.setOffsetY(3.0f);
        ds.setColor(Color.color(0.0f, 0.0f, 0.0f, 0.6f));
        titleLabel.setEffect(ds);

        Label subtitleLabel = new Label("TACTICAL DEFENSE PROTOCOL // PRTS-v1.2");
        subtitleLabel.setStyle("-fx-text-fill: #ff9b00; -fx-font-size: 11px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");

        Rectangle decBar = new Rectangle(200, 3, Color.web("#ff9b00"));

        VBox statusBox = new VBox(5);
        statusBox.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 10px; -fx-background-radius: 4px; -fx-border-color: rgba(255,255,255,0.1); -fx-border-width: 1px;");
        
        Label status1 = new Label("SYSTEM STATUS: ONLINE");
        status1.setStyle("-fx-text-fill: #28a745; -fx-font-size: 11px; -fx-font-family: 'monospace'; -fx-font-weight: bold;");
        Label status2 = new Label("LINK STABILITY: 98.4%");
        status2.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 11px; -fx-font-family: 'monospace';");
        Label status3 = new Label("SQUAD DATA SYNCED");
        status3.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 11px; -fx-font-family: 'monospace';");
        Label status4 = new Label("WARNING: INCOMING THREAT DETECTED");
        status4.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 11px; -fx-font-family: 'monospace'; -fx-font-weight: bold;");

        statusBox.getChildren().addAll(status1, status2, status3, status4);

        infoPanel.getChildren().addAll(titleLabel, subtitleLabel, decBar, statusBox);
        root.getChildren().add(infoPanel);

        // Right Panel - Action Buttons
        VBox btnPanel = new VBox(20);
        btnPanel.setLayoutX(440);
        btnPanel.setLayoutY(180);
        btnPanel.setPrefWidth(300);

        Button terminalBtn = createMenuButton("TERMINAL", "CHOOSE OPERATION STAGE", "#ff9b00", 0);
        terminalBtn.setOnAction(e -> showStageSelect(stage));

        Button operatorsBtn = createMenuButton("OPERATORS", "SQUAD ARCHIVES [DEV]", "#cccccc", 20);
        operatorsBtn.setOnAction(e -> showOperatorScreen(stage));

        Button exitBtn = createMenuButton("EXIT SYSTEM", "TERMINATE LINK", "#dc3545", 40);
        exitBtn.setOnAction(e -> {
            stage.close();
            System.exit(0);
        });

        btnPanel.getChildren().addAll(terminalBtn, operatorsBtn, exitBtn);
        root.getChildren().add(btnPanel);

        Scene scene = new Scene(root, 800, 650);
        stage.setTitle("Arclights - Main Terminal");
        stage.setScene(scene);
        stage.show();
    }

    private Button createMenuButton(String mainText, String subText, String accentColorHex, double translateX) {
        Button btn = new Button();
        btn.setPrefSize(280, 65);
        btn.setTranslateX(translateX);

        VBox textContainer = new VBox(3);
        textContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label mainLbl = new Label(mainText);
        mainLbl.setStyle("-fx-text-fill: inherit; -fx-font-size: 18px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
        
        Label subLbl = new Label(subText);
        subLbl.setStyle("-fx-text-fill: inherit; -fx-font-size: 9px; -fx-font-family: 'Arial'; -fx-opacity: 0.7; -fx-font-weight: bold;");
        
        textContainer.getChildren().addAll(mainLbl, subLbl);
        btn.setGraphic(textContainer);

        String baseStyle = 
            "-fx-background-color: rgba(20, 22, 25, 0.85); " +
            "-fx-text-fill: #ffffff; " +
            "-fx-border-color: " + accentColorHex + "; " +
            "-fx-border-width: 0 0 0 6px; " +
            "-fx-alignment: center-left; " +
            "-fx-padding: 10px 20px 10px 20px; " +
            "-fx-background-radius: 4px; " +
            "-fx-cursor: hand;";
            
        String hoverStyle = 
            "-fx-background-color: " + accentColorHex + "; " +
            "-fx-text-fill: " + (accentColorHex.equals("#cccccc") || accentColorHex.equals("#ff9b00") ? "#000000" : "#ffffff") + "; " +
            "-fx-border-color: #ffffff; " +
            "-fx-border-width: 0 0 0 6px; " +
            "-fx-alignment: center-left; " +
            "-fx-padding: 10px 20px 10px 20px; " +
            "-fx-background-radius: 4px; " +
            "-fx-cursor: hand;";

        btn.setStyle(baseStyle);

        btn.setOnMouseEntered(e -> {
            btn.setStyle(hoverStyle);
            btn.setTranslateX(translateX - 5);
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(baseStyle);
            btn.setTranslateX(translateX);
        });

        return btn;
    }

    private void showStageSelect(Stage stage) {
        Pane root = new Pane();
        root.setPrefSize(800, 650);

        ImageView bgView = null;
        try {
            InputStream bgStream = getClass().getResourceAsStream("background.png");
            if (bgStream != null) {
                Image bgImage = new Image(bgStream);
                bgView = new ImageView(bgImage);
                bgView.setFitWidth(800);
                bgView.setFitHeight(650);
                bgView.setPreserveRatio(false);
            }
        } catch (Exception e) {}

        if (bgView != null) {
            root.getChildren().add(bgView);
        } else {
            Rectangle fallbackBg = new Rectangle(800, 650, Color.web("#0d0f12"));
            root.getChildren().add(fallbackBg);
        }

        Rectangle tint = new Rectangle(800, 650);
        tint.setFill(Color.rgb(10, 12, 15, 0.8));
        root.getChildren().add(tint);

        Button backBtn = new Button("<- BACK TO MAIN MENU");
        backBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #cccccc; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px; " +
            "-fx-cursor: hand;"
        );
        backBtn.setLayoutX(40);
        backBtn.setLayoutY(40);
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #ff9b00; -fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;"
        ));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #cccccc; -fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;"
        ));
        backBtn.setOnAction(e -> showStartMenu(stage));
        root.getChildren().add(backBtn);

        Label pageTitle = new Label("TERMINAL / 终端");
        pageTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 26px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
        pageTitle.setLayoutX(40);
        pageTitle.setLayoutY(80);

        Label pageSubtitle = new Label("SELECT PREPARATION SYSTEM OPERATIONS");
        pageSubtitle.setStyle("-fx-text-fill: #ff9b00; -fx-font-size: 10px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
        pageSubtitle.setLayoutX(40);
        pageSubtitle.setLayoutY(115);

        root.getChildren().addAll(pageTitle, pageSubtitle);

        HBox cardsBox = new HBox(40);
        cardsBox.setLayoutX(60);
        cardsBox.setLayoutY(160);
        cardsBox.setPrefWidth(680);
        cardsBox.setAlignment(Pos.CENTER);

        Pane card1 = createStageCard("01-01", "TRAINING GROUND", "Standard operation layout for new doctor onboarding. Practice unit selection and coverage alignment.", MapPresets.LEVEL_1, "#ff9b00", stage);
        Pane card2 = createStageCard("01-02", "NARROW PASSAGE", "Defensive tactics exercise in a constrained corridor. Optimize blocker positioning and ranged support lanes.", MapPresets.LEVEL_2, "#00a2ff", stage);

        cardsBox.getChildren().addAll(card1, card2);
        root.getChildren().add(cardsBox);

        Scene scene = new Scene(root, 800, 650);
        stage.setScene(scene);
    }

    private Pane createStageCard(String code, String name, String desc, char[][] layout, String accentColor, Stage stage) {
        VBox card = new VBox(15);
        card.setPrefSize(310, 420);
        card.setStyle(
            "-fx-background-color: rgba(20, 22, 25, 0.9); " +
            "-fx-border-color: rgba(255, 255, 255, 0.1); " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-padding: 25px;"
        );

        HBox header = new HBox();
        header.setAlignment(Pos.BASELINE_LEFT);
        header.setSpacing(10);
        
        Label codeLbl = new Label(code);
        codeLbl.setStyle("-fx-text-fill: " + accentColor + "; -fx-font-size: 32px; -fx-font-family: 'Arial'; -fx-font-weight: 900;");
        
        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
        
        header.getChildren().addAll(codeLbl, nameLbl);

        Label descLbl = new Label(desc);
        descLbl.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 11px; -fx-font-family: 'Arial'; -fx-wrap-text: true;");
        descLbl.setPrefHeight(60);

        VBox previewBox = new VBox(5);
        previewBox.setAlignment(Pos.CENTER);
        previewBox.setPrefHeight(160);
        previewBox.setStyle("-fx-background-color: rgba(0,0,0,0.4); -fx-padding: 10px; -fx-background-radius: 4px; -fx-border-color: rgba(255,255,255,0.05); -fx-border-width: 1px;");

        Label previewTitle = new Label("OPERATION MAP SCHEMATIC");
        previewTitle.setStyle("-fx-text-fill: #666666; -fx-font-size: 9px; -fx-font-family: 'monospace'; -fx-font-weight: bold;");
        previewBox.getChildren().add(previewTitle);

        int rows = layout.length;
        int cols = layout[0].length;
        int miniCell = 15;
        int miniGap = 1;
        
        VBox miniGrid = new VBox(miniGap);
        miniGrid.setAlignment(Pos.CENTER);
        for (int r = 0; r < rows; r++) {
            HBox miniRow = new HBox(miniGap);
            miniRow.setAlignment(Pos.CENTER);
            for (int c = 0; c < cols; c++) {
                Rectangle cell = new Rectangle(miniCell, miniCell);
                char type = layout[r][c];
                if (type == 'H') {
                    cell.setFill(Color.DARKGRAY);
                } else if (type == 'S') {
                    cell.setFill(Color.RED);
                } else if (type == 'O') {
                    cell.setFill(Color.BLUE);
                } else if (type == 'M') {
                    cell.setFill(Color.LIGHTGRAY);
                } else {
                    cell.setFill(Color.LIGHTPINK);
                }
                miniRow.getChildren().add(cell);
            }
            miniGrid.getChildren().add(miniRow);
        }
        previewBox.getChildren().add(miniGrid);

        Button deployBtn = new Button("DEPLOY / 出击");
        deployBtn.setPrefWidth(260);
        deployBtn.setPrefHeight(45);
        deployBtn.setStyle(
            "-fx-background-color: " + accentColor + "; " +
            "-fx-text-fill: " + (accentColor.equals("#ffffff") || accentColor.equals("#cccccc") || accentColor.equals("#ff9b00") ? "#000000" : "#ffffff") + "; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 14px; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 4px;"
        );
        deployBtn.setOnMouseEntered(e -> deployBtn.setStyle(
            "-fx-background-color: #ffffff; " +
            "-fx-text-fill: #000000; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 14px; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 4px;"
        ));
        deployBtn.setOnMouseExited(e -> deployBtn.setStyle(
            "-fx-background-color: " + accentColor + "; " +
            "-fx-text-fill: " + (accentColor.equals("#ffffff") || accentColor.equals("#cccccc") || accentColor.equals("#ff9b00") ? "#000000" : "#ffffff") + "; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 14px; " +
            "-fx-cursor: hand; " +
            "-fx-background-radius: 4px;"
        ));
        deployBtn.setOnAction(e -> startGame(stage, layout, name));

        card.getChildren().addAll(header, descLbl, previewBox, deployBtn);

        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: rgba(25, 28, 32, 0.95); " +
            "-fx-border-color: " + accentColor + "; " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-padding: 25px;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: rgba(20, 22, 25, 0.9); " +
            "-fx-border-color: rgba(255, 255, 255, 0.1); " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-padding: 25px;"
        ));

        return card;
    }

    private void showOperatorScreen(Stage stage) {
        Pane root = new Pane();
        root.setPrefSize(800, 650);

        ImageView bgView = null;
        try {
            InputStream bgStream = getClass().getResourceAsStream("background.png");
            if (bgStream != null) {
                Image bgImage = new Image(bgStream);
                bgView = new ImageView(bgImage);
                bgView.setFitWidth(800);
                bgView.setFitHeight(650);
                bgView.setPreserveRatio(false);
            }
        } catch (Exception e) {}

        if (bgView != null) {
            root.getChildren().add(bgView);
        } else {
            Rectangle fallbackBg = new Rectangle(800, 650, Color.web("#0d0f12"));
            root.getChildren().add(fallbackBg);
        }

        Rectangle tint = new Rectangle(800, 650);
        tint.setFill(Color.rgb(10, 12, 15, 0.85));
        root.getChildren().add(tint);

        Button backBtn = new Button("<- BACK TO MAIN MENU");
        backBtn.setStyle(
            "-fx-background-color: transparent; " +
            "-fx-text-fill: #cccccc; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 12px; " +
            "-fx-cursor: hand;"
        );
        backBtn.setLayoutX(40);
        backBtn.setLayoutY(40);
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #ff9b00; -fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;"
        ));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #cccccc; -fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;"
        ));
        backBtn.setOnAction(e -> showStartMenu(stage));
        root.getChildren().add(backBtn);

        Label pageTitle = new Label("OPERATOR ARCHIVES / 干员档案");
        pageTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 26px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
        pageTitle.setLayoutX(40);
        pageTitle.setLayoutY(80);

        Label pageSubtitle = new Label("OPERATIONAL UNIT ARCHIVES AND SQUAD MANAGEMENT");
        pageSubtitle.setStyle("-fx-text-fill: #ff9b00; -fx-font-size: 10px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
        pageSubtitle.setLayoutX(40);
        pageSubtitle.setLayoutY(115);

        root.getChildren().addAll(pageTitle, pageSubtitle);

        VBox archiveBox = new VBox(20);
        archiveBox.setLayoutX(60);
        archiveBox.setLayoutY(160);
        archiveBox.setPrefSize(680, 420);
        archiveBox.setStyle(
            "-fx-background-color: rgba(10, 12, 15, 0.95); " +
            "-fx-border-color: rgba(255, 255, 255, 0.05); " +
            "-fx-border-width: 1px; " +
            "-fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; " +
            "-fx-padding: 30px;"
        );

        HBox warningBanner = new HBox(15);
        warningBanner.setAlignment(Pos.CENTER_LEFT);
        warningBanner.setStyle(
            "-fx-background-color: rgba(220, 53, 69, 0.1); " +
            "-fx-border-color: #dc3545; " +
            "-fx-border-width: 1px; " +
            "-fx-padding: 15px; " +
            "-fx-background-radius: 4px;"
        );

        Label warningIcon = new Label("[!]");
        warningIcon.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 18px; -fx-font-weight: bold; -fx-font-family: 'monospace';");
        
        VBox warningText = new VBox(3);
        Label warningTitle = new Label("ACCESS RESTRICTED / CORE OFFLINE");
        warningTitle.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 13px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
        Label warningDesc = new Label("The operator deployment configuration files are locked. Complete current main training phases to unlock system access.");
        warningDesc.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 10px; -fx-font-family: 'Arial';");
        warningText.getChildren().addAll(warningTitle, warningDesc);
        
        warningBanner.getChildren().addAll(warningIcon, warningText);
        archiveBox.getChildren().add(warningBanner);

        Label infoTitle = new Label("AVAILABLE CLASS PROFILES (CURRENT LEVEL LOG)");
        infoTitle.setStyle("-fx-text-fill: #ff9b00; -fx-font-size: 12px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
        
        HBox classProfiles = new HBox(30);
        classProfiles.setAlignment(Pos.CENTER);
        
        VBox sniperProfile = createClassProfile("SNIPER", "RANGED / PHYSICAL", "High speed single-target physical sniper. Deployed on high ground tiles to target incoming enemy drones and lightweight leaders.", Color.GREEN);
        VBox defenderProfile = createClassProfile("DEFENDER", "MELEE / DEFENSE", "Heavy blocker unit. Deployed on melee road tiles to intercept and stall up to 3 enemy units simultaneously.", Color.BLUE);
        
        classProfiles.getChildren().addAll(sniperProfile, defenderProfile);
        
        archiveBox.getChildren().addAll(infoTitle, classProfiles);
        root.getChildren().add(archiveBox);

        Scene scene = new Scene(root, 800, 650);
        stage.setScene(scene);
    }

    private VBox createClassProfile(String title, String type, String desc, Color color) {
        VBox profile = new VBox(10);
        profile.setPrefWidth(290);
        profile.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.02); " +
            "-fx-border-color: rgba(255, 255, 255, 0.1); " +
            "-fx-border-width: 1px; " +
            "-fx-background-radius: 4px; " +
            "-fx-border-radius: 4px; " +
            "-fx-padding: 15px;"
        );

        HBox pHeader = new HBox(10);
        pHeader.setAlignment(Pos.CENTER_LEFT);
        
        Rectangle icon = new Rectangle(12, 12, color);
        
        Label pTitle = new Label(title);
        pTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
        
        Label pType = new Label("(" + type + ")");
        pType.setStyle("-fx-text-fill: #ff9b00; -fx-font-size: 9px; -fx-font-family: 'monospace';");

        pHeader.getChildren().addAll(icon, pTitle, pType);

        Label pDesc = new Label(desc);
        pDesc.setStyle("-fx-text-fill: #999999; -fx-font-size: 10px; -fx-font-family: 'Arial'; -fx-wrap-text: true;");
        pDesc.setPrefHeight(60);

        profile.getChildren().addAll(pHeader, pDesc);
        return profile;
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