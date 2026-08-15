package com.arclights.ui;

import java.util.LinkedHashMap;
import java.util.Map;

import com.arclights.models.OperatorCatalog;
import com.arclights.models.PlayerProgress;

import javafx.geometry.Insets;
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

    private static final double CARD_SIZE = 90;

    private final HBox rootContainer;
    private final HBox cardsDeck;
    private final Label dpLabel;

    private final Map<String, Pane> cardsByOperatorId = new LinkedHashMap<>();

    public OperatorDeploymentBar(String levelName) {
        rootContainer = new HBox();
        rootContainer.setLayoutY(UILoader.WINDOW_HEIGHT - 90 - 35);
        rootContainer.setPrefHeight(90);

        VBox infoBox = new VBox(1);

        dpLabel = new Label("DP: 0 / 0");
        dpLabel.setStyle(
            "-fx-text-fill: #ffffff; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 16px; " +
            "-fx-background-color: rgba(0, 0, 0, 0.55); " +
            "-fx-padding: 6px 12px;"
        );
        dpLabel.setPadding(new Insets(0, 0, 4, 0));
        infoBox.getChildren().add(dpLabel);
        infoBox.setAlignment(Pos.BOTTOM_RIGHT);

        cardsDeck = new HBox();
        cardsDeck.setAlignment(Pos.BOTTOM_RIGHT);
        rebuildCards();

        VBox cardsDP = new VBox(infoBox, cardsDeck);

        rootContainer.getChildren().addAll(cardsDP);
    }

    /**
     * (Re)builds the deployment cards from every operator the player
     * currently owns (starter operators + anything recruited in the shop),
     * in catalog order. Also repositions the bar so it stays right-aligned
     * no matter how many cards are shown.
     */
    private void rebuildCards() {
        cardsDeck.getChildren().clear();
        cardsByOperatorId.clear();

        for (OperatorCatalog.Definition def : OperatorCatalog.all()) {
            if (!PlayerProgress.ownsOperator(def.id)) continue;
            Pane card = createOperatorCard(def);
            cardsByOperatorId.put(def.id, card);
            cardsDeck.getChildren().add(card);
        }

        int cardCount = Math.max(1, cardsByOperatorId.size());
        rootContainer.setLayoutX(UILoader.WINDOW_WIDTH - CARD_SIZE * cardCount - 1 * cardCount);
    }

    private Pane createOperatorCard(OperatorCatalog.Definition def) {
        StackPane cardRoot = new StackPane();
        cardRoot.setPrefSize(CARD_SIZE, CARD_SIZE);
        cardRoot.setMaxSize(CARD_SIZE, CARD_SIZE);
        cardRoot.setStyle("-fx-cursor: hand;");

        Rectangle border = new Rectangle(CARD_SIZE, CARD_SIZE);
        border.setFill(Color.web("#1e232a"));
        border.setStroke(Color.web("#ffffff"));
        border.setStrokeWidth(1.5);
        border.setOpacity(0.2);

        Image img = UILoader.loadImage(def.portraitPath);
        ImageView portrait = new ImageView();
        if (img != null) {
            portrait.setImage(img);
            portrait.setFitWidth(CARD_SIZE - 8);
            portrait.setFitHeight(CARD_SIZE - 8);
            portrait.setPreserveRatio(true);
        } else {
            Rectangle placeholder = new Rectangle(CARD_SIZE - 10, CARD_SIZE - 10, def.cardColor);
            cardRoot.getChildren().add(placeholder);
        }

        Label costLabel = new Label(String.valueOf(def.deployCost));
        costLabel.setStyle(
            "-fx-text-fill: #ffffff; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 13px; " +
            "-fx-background-color: rgba(0, 0, 0, 0.65); " +
            "-fx-padding: 1px 5px; " +
            "-fx-background-radius: 3px;"
        );
        StackPane.setAlignment(costLabel, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(costLabel, new Insets(0, 3, 3, 0));

        cardRoot.getChildren().addAll(border, portrait, costLabel);
        return cardRoot;
    }

    /**
     * Refreshes the DP counter and dims any operator card the player can no
     * longer afford, mirroring Arknights' deployment-cost feedback.
     */
    public void updateDeploymentPoints(int currentDP, int maxDP) {
        dpLabel.setText("DP: " + currentDP + " / " + maxDP);
        for (Map.Entry<String, Pane> entry : cardsByOperatorId.entrySet()) {
            OperatorCatalog.Definition def = OperatorCatalog.get(entry.getKey());
            boolean affordable = def != null && currentDP >= def.deployCost;
            setCardAffordable(entry.getValue(), affordable);
        }
    }

    private void setCardAffordable(Pane card, boolean affordable) {
        card.setOpacity(affordable ? 1.0 : 0.4);
        card.setStyle(affordable ? "-fx-cursor: hand;" : "-fx-cursor: default;");
    }

    public HBox getRoot() {
        return rootContainer;
    }

    /** Operator id -> its card node, for every operator currently shown in the bar. */
    public Map<String, Pane> getOperatorCards() {
        return cardsByOperatorId;
    }
}
