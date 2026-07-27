package com.arclights.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class OperatorDeploymentBar {

    private final HBox rootContainer;
    private Pane sniperGroup;
    private Pane defenderGroup;

    public OperatorDeploymentBar(String levelName) {
        rootContainer = new HBox(15);
        rootContainer.setAlignment(Pos.CENTER_LEFT);
        rootContainer.setStyle(
            "-fx-background-color: rgba(18, 18, 18, 0.85); " +
            "-fx-border-color: rgba(255, 255, 255, 0.15); " +
            "-fx-border-width: 1px 0 0 0; " +
            "-fx-padding: 10px 20px;"
        );
        
        rootContainer.setLayoutX(20);
        rootContainer.setLayoutY(UILoader.WINDOW_HEIGHT - 100);
        rootContainer.setPrefHeight(90);

        VBox infoBox = new VBox(2);
        infoBox.setAlignment(Pos.CENTER_LEFT);
        Label stageLabel = new Label("OPERATION: " + levelName.toUpperCase());
        stageLabel.setStyle("-fx-text-fill: #ff9b00; -fx-font-weight: bold; -fx-font-size: 11px;");
        
        Label hintLabel = new Label("DRAG OPERATOR TO DEPLOY");
        hintLabel.setStyle("-fx-text-fill: #888888; -fx-font-weight: bold; -fx-font-size: 10px;");
        infoBox.getChildren().addAll(stageLabel, hintLabel);

        sniperGroup = createOperatorCard("SNIPER", "/images/operators/sniper.png", Color.web("#4caf50"));
        defenderGroup = createOperatorCard("DEFENDER", "/images/operators/defender.png", Color.web("#2196f3"));

        HBox cardsDeck = new HBox(12, sniperGroup, defenderGroup);
        cardsDeck.setAlignment(Pos.CENTER_LEFT);

        rootContainer.getChildren().addAll(infoBox, cardsDeck);
    }

    private Pane createOperatorCard(String roleName, String imagePath, Color themeColor) {
        double size = 70;

        StackPane cardRoot = new StackPane();
        cardRoot.setPrefSize(size, size);
        cardRoot.setMaxSize(size, size);
        cardRoot.setStyle("-fx-cursor: hand;");

        Rectangle border = new Rectangle(size, size);
        border.setFill(Color.web("#1e232a"));
        border.setStroke(Color.web("#3a424d"));
        border.setStrokeWidth(1.5);
        border.setArcWidth(4);
        border.setArcHeight(4);

        Image img = UILoader.loadImage(imagePath);
        ImageView portrait = new ImageView();
        if (img != null) {
            portrait.setImage(img);
            portrait.setFitWidth(size - 8);
            portrait.setFitHeight(size - 8);
            portrait.setPreserveRatio(true);
        } else {
            // Fallback square color block if image doesn't exist yet
            Rectangle placeholder = new Rectangle(size - 10, size - 10, themeColor);
            cardRoot.getChildren().add(placeholder);
        }

        // Top-left Class Accent Strip
        Rectangle roleAccent = new Rectangle(size, 4, themeColor);
        roleAccent.setTranslateY(-size / 2 + 2);

        // Bottom Class Title Badge
        Label roleLabel = new Label(roleName);
        roleLabel.setStyle(
            "-fx-text-fill: white; " +
            "-fx-font-size: 9px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-color: rgba(0, 0, 0, 0.7); " +
            "-fx-padding: 1px 4px;"
        );
        roleLabel.setTranslateY(size / 2 - 10);

        cardRoot.getChildren().addAll(border, portrait, roleAccent, roleLabel);

        // Arknights Hover Effects
        cardRoot.setOnMouseEntered(e -> {
            border.setStroke(themeColor);
            cardRoot.setScaleX(1.05);
            cardRoot.setScaleY(1.05);
        });

        cardRoot.setOnMouseExited(e -> {
            border.setStroke(Color.web("#3a424d"));
            cardRoot.setScaleX(1.0);
            cardRoot.setScaleY(1.0);
        });

        return cardRoot;
    }

    public HBox getRoot() {
        return rootContainer;
    }

    public Pane getSniperGroup() {
        return sniperGroup;
    }

    public Pane getDefenderGroup() {
        return defenderGroup;
    }
}