package com.arclights.ui;

import javafx.geometry.Pos;
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
        rootContainer = new HBox();
        
        rootContainer.setLayoutX(UILoader.WINDOW_WIDTH - 91 * 2);
        rootContainer.setLayoutY(UILoader.WINDOW_HEIGHT - 90);
        rootContainer.setPrefHeight(90);

        VBox infoBox = new VBox(1);

        sniperGroup = createOperatorCard("/com/arclights/char/char_124_kroos_sale#14 #15005.png", Color.web("#4caf50"));
        defenderGroup = createOperatorCard("/com/arclights/char/char_122_beagle_boc#1 #15657.png", Color.web("#2196f3"));

        HBox cardsDeck = new HBox(sniperGroup, defenderGroup);
        cardsDeck.setAlignment(Pos.BOTTOM_RIGHT);

        rootContainer.getChildren().addAll(infoBox, cardsDeck);
    }

    private Pane createOperatorCard(String imagePath, Color themeColor) {
        double size = 90;

        StackPane cardRoot = new StackPane();
        cardRoot.setPrefSize(size, size);
        cardRoot.setMaxSize(size, size);
        cardRoot.setStyle("-fx-cursor: hand;");

        Rectangle border = new Rectangle(size, size);
        border.setFill(Color.web("#1e232a"));
        border.setStroke(Color.web("#ffffff"));
        border.setStrokeWidth(1.5);
        border.setOpacity(0.2);

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

        cardRoot.getChildren().addAll(border, portrait);

        cardRoot.setOnMouseEntered(e -> {

        });

        cardRoot.setOnMouseExited(e -> {

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