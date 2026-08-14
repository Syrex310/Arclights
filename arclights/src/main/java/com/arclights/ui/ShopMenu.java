package com.arclights.ui;

import com.arclights.models.PlayerProgress;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class ShopMenu {

    public interface ShopCallbacks {
        void onBackToMenu();
        void onPurchaseComplete();
    }

    private static final OperatorOffer[] OFFERS = {
        new OperatorOffer("placeholder_operator_1", "PLACEHOLDER 01", "SNIPER", 0),
        new OperatorOffer("placeholder_operator_2", "PLACEHOLDER 02", "DEFENDER", 0),
        new OperatorOffer("placeholder_operator_3", "PLACEHOLDER 03", "CASTER", 1000),
        new OperatorOffer("placeholder_operator_4", "PLACEHOLDER 04", "MEDIC", 1250),
        new OperatorOffer("placeholder_operator_5", "PLACEHOLDER 05", "GUARD", 1500),
        new OperatorOffer("placeholder_operator_6", "PLACEHOLDER 06", "SPECIALIST", 1750),
        new OperatorOffer("placeholder_operator_7", "PLACEHOLDER 07", "VANGUARD", 2000),
        new OperatorOffer("placeholder_operator_8", "PLACEHOLDER 08", "SUPPORTER", 2500)
    };

    public static Scene createScene(Runnable onBackToMenu, Runnable onPurchaseComplete) {
        Pane root = new Pane();
        root.setPrefSize(UILoader.WINDOW_WIDTH, UILoader.WINDOW_HEIGHT);

        UILoader.loadBackground(root, "/com/arclights/background.png", Color.web("#0d0f12"));
        UILoader.addTintOverlay(root, 0.86);
        UILoader.addPageHeader(root, "OPERATOR SHOP", "RECRUIT NEW OPERATORS");

        Button backBtn = UILoader.createBackButton(onBackToMenu);
        root.getChildren().add(backBtn);

        Label crystalLabel = new Label();
        crystalLabel.textProperty().bind(
            javafx.beans.binding.Bindings.concat("\uD83D\uDC8E  ", PlayerProgress.crystalsProperty().asString())
        );
        crystalLabel.setStyle(
            "-fx-text-fill: #7fdfff; -fx-font-size: 20px; -fx-font-weight: bold; " +
            "-fx-background-color: rgba(0, 0, 0, 0.55); -fx-background-radius: 6px; " +
            "-fx-padding: 8px 14px;"
        );
        crystalLabel.setLayoutX(1030);
        crystalLabel.setLayoutY(38);
        root.getChildren().add(crystalLabel);

        FlowPane shopGrid = new FlowPane();
        shopGrid.setLayoutX(60);
        shopGrid.setLayoutY(155);
        shopGrid.setPrefWidth(1100);
        shopGrid.setPrefHeight(450);
        shopGrid.setHgap(18);
        shopGrid.setVgap(18);

        for (OperatorOffer offer : OFFERS) {
            if (!PlayerProgress.ownsOperator(offer.id)) {
                shopGrid.getChildren().add(createOfferCard(offer, onPurchaseComplete));
            }
        }

        if (shopGrid.getChildren().isEmpty()) {
            Label soldOut = new Label("ALL PLACEHOLDER OPERATORS RECRUITED");
            soldOut.setStyle("-fx-text-fill: #2ecc71; -fx-font-size: 20px; -fx-font-weight: bold;");
            soldOut.setLayoutX(60);
            soldOut.setLayoutY(190);
            root.getChildren().add(soldOut);
        } else {
            root.getChildren().add(shopGrid);
        }

        return new Scene(root, UILoader.WINDOW_WIDTH, UILoader.WINDOW_HEIGHT);
    }

    private static VBox createOfferCard(OperatorOffer offer, Runnable onPurchaseComplete) {
        VBox card = new VBox(9);
        card.setPrefSize(245, 190);
        card.setAlignment(Pos.TOP_CENTER);
        card.setStyle(
            "-fx-background-color: rgba(10, 12, 15, 0.96); " +
            "-fx-border-color: rgba(255, 255, 255, 0.12); " +
            "-fx-border-width: 1px; -fx-background-radius: 5px; " +
            "-fx-border-radius: 5px; -fx-padding: 15px;"
        );

        Rectangle icon = new Rectangle(55, 55, Color.web("#555b66"));

        Label name = new Label(offer.name);
        name.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label type = new Label(offer.type);
        type.setStyle("-fx-text-fill: #ff9b00; -fx-font-size: 10px; -fx-font-weight: bold;");

        Label price = new Label("\uD83D\uDC8E " + offer.cost + " CRYSTALS");
        price.setStyle("-fx-text-fill: #7fdfff; -fx-font-size: 11px; -fx-font-weight: bold;");

        Button buy = new Button("RECRUIT");
        buy.setPrefWidth(170);
        buy.setStyle(
            "-fx-background-color: #ff9b00; -fx-text-fill: #111111; " +
            "-fx-font-weight: bold; -fx-cursor: hand;"
        );

        Label status = new Label();
        status.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 10px; -fx-font-weight: bold;");

        buy.setOnAction(e -> {
            if (PlayerProgress.purchaseOperator(offer.id, offer.cost)) {
                // Rebuild the shop so the purchased operator disappears immediately.
                onPurchaseComplete.run();
            } else {
                status.setText("NOT ENOUGH CRYSTALS");
            }
        });

        HBox priceRow = new HBox(8, price);
        priceRow.setAlignment(Pos.CENTER);

        card.getChildren().addAll(icon, name, type, priceRow, buy, status);
        return card;
    }

    private static class OperatorOffer {
        final String id;
        final String name;
        final String type;
        final int cost;

        OperatorOffer(String id, String name, String type, int cost) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.cost = cost;
        }
    }
}
