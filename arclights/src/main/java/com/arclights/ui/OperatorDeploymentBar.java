package com.arclights.ui;

import com.arclights.entity.operator.Defender;
import com.arclights.entity.operator.Sniper;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class OperatorDeploymentBar {

    private final HBox rootContainer;
    private Pane sniperGroup;
    private Pane defenderGroup;
    private final Label dpLabel;

    public OperatorDeploymentBar(String levelName) {
        rootContainer = new HBox();
        
        rootContainer.setLayoutX(UILoader.WINDOW_WIDTH - 91 * 2);
        rootContainer.setLayoutY(UILoader.WINDOW_HEIGHT - 90 - 35);
        rootContainer.setPrefHeight(90);

        VBox infoBox = new VBox(1);

        dpLabel = new Label("DP: 0 / 0");
        dpLabel.setStyle(
            "-fx-text-fill: #ffd54f; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-background-color: rgba(0, 0, 0, 0.55); " +
            "-fx-padding: 6px 12px;"
        );
        dpLabel.setPadding(new Insets(0, 0, 4, 0));
        infoBox.getChildren().add(dpLabel);
        infoBox.setAlignment(Pos.BOTTOM_LEFT);

        sniperGroup = createOperatorCard("/com/arclights/char/char_124_kroos_sale#14 #15005.png", Color.web("#4caf50"), Sniper.DEPLOY_COST);
        defenderGroup = createOperatorCard("/com/arclights/char/char_122_beagle_boc#1 #15657.png", Color.web("#2196f3"), Defender.DEPLOY_COST);

        HBox cardsDeck = new HBox(sniperGroup, defenderGroup);

        VBox cardsDP = new VBox(infoBox, cardsDeck);
        cardsDeck.setAlignment(Pos.BOTTOM_RIGHT);

        rootContainer.getChildren().addAll(cardsDP);
    }

    private Pane createOperatorCard(String imagePath, Color themeColor, int dpCost) {
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

        Label costLabel = new Label(String.valueOf(dpCost));
        costLabel.setStyle(
            "-fx-text-fill: #ffd54f; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 13px; " +
            "-fx-background-color: rgba(0, 0, 0, 0.65); " +
            "-fx-padding: 1px 5px; " +
            "-fx-background-radius: 3px;"
        );
        StackPane.setAlignment(costLabel, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(costLabel, new Insets(0, 3, 3, 0));

        cardRoot.getChildren().addAll(border, portrait, costLabel);

        cardRoot.setOnMouseEntered(e -> {

        });

        cardRoot.setOnMouseExited(e -> {

        });

        return cardRoot;
    }

    /**
     * Refreshes the DP counter and dims any operator card the player can no
     * longer afford, mirroring Arknights' deployment-cost feedback.
     */
    public void updateDeploymentPoints(int currentDP, int maxDP) {
        dpLabel.setText("DP: " + currentDP + " / " + maxDP);
        setCardAffordable(sniperGroup, currentDP >= Sniper.DEPLOY_COST);
        setCardAffordable(defenderGroup, currentDP >= Defender.DEPLOY_COST);
    }

    private void setCardAffordable(Pane card, boolean affordable) {
        card.setOpacity(affordable ? 1.0 : 0.4);
        card.setStyle(affordable ? "-fx-cursor: hand;" : "-fx-cursor: default;");
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