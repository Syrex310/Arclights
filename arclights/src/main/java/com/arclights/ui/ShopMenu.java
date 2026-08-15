package com.arclights.ui;

import com.arclights.models.PlayerProgress;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ShopMenu {

    private static final String[] OPERATOR_PORTRAITS = {
        "/com/arclights/char/char_124_kroos_sale#14 #15005.png",
        "/com/arclights/char/char_122_beagle_boc#1 #15657.png",
        "/com/arclights/char/char_180_amgoat_summer#5 #15978.png",
        "/com/arclights/char/char_120_hibisc_nian#1 #15736.png",
        "/com/arclights/char/char_017_huang_witch#5 #14843.png",
        "/com/arclights/char/char_144_red_summer#6 #16091.png",
        "/com/arclights/char/char_1036_fang2_snow#8 #15485.png",
        "/com/arclights/char/char_1012_skadi2_boc#4 #15713.png"
    };

    public interface ShopCallbacks {
        void onBackToMenu();
        void onPurchaseComplete();
    }

    private static final OperatorOffer[] OFFERS = {
        new OperatorOffer("placeholder_operator_1", "Kroos", "SNIPER", 0, "/com/arclights/char/char_124_kroos_sale#14 #15005.png"),
        new OperatorOffer("placeholder_operator_2", "Beagle", "DEFENDER", 0, "/com/arclights/char/char_122_beagle_boc#1 #15657.png"),
        new OperatorOffer("placeholder_operator_7", "Fang", "VANGUARD", 1000, "/com/arclights/char/char_1036_fang2_snow#8 #15485.png"),
        new OperatorOffer("placeholder_operator_3", "Eyjafjalla", "CASTER", 2500, "/com/arclights/char/char_180_amgoat_summer#5 #15978.png"),
        new OperatorOffer("placeholder_operator_4", "Hibicus", "MEDIC", 1500, "/com/arclights/char/char_120_hibisc_nian#1 #15736.png"),
        new OperatorOffer("placeholder_operator_5", "Blaze", "GUARD", 5000, "/com/arclights/char/char_017_huang_witch#5 #14843.png"),
        new OperatorOffer("placeholder_operator_6", "Projekt Red", "SPECIALIST", 1500, "/com/arclights/char/char_1036_fang2_snow#8 #15485.png"),
        new OperatorOffer("placeholder_operator_8", "Skadi", "SUPPORTER", 2000, "/com/arclights/char/char_1012_skadi2_boc#4 #15713.png")
    };

    public static Scene createScene(Runnable onBackToMenu, Runnable onPurchaseComplete) {
        Pane root = new Pane();
        root.setPrefSize(UILoader.WINDOW_WIDTH, UILoader.WINDOW_HEIGHT);

        UILoader.loadBackground(root, "/com/arclights/background2.png", Color.web("#0d0f12"));
        UILoader.addTintOverlay(root, 0.86);
        UILoader.addPageHeader(root, "OPERATOR SHOP", "RECRUIT NEW OPERATORS");

        Button backBtn = UILoader.createBackButton(onBackToMenu);
        root.getChildren().add(backBtn);

        Label crystalLabel = new Label();
        crystalLabel.textProperty().bind(
            javafx.beans.binding.Bindings.concat("\uD83D\uDC8E  ", PlayerProgress.crystalsProperty().asString())
        );
        crystalLabel.setStyle(
            "-fx-text-fill: #ff1e78; -fx-font-size: 20px; -fx-font-weight: bold; " +
            "-fx-background-color: rgba(0, 0, 0, 0.55); -fx-background-radius: 6px; " +
            "-fx-padding: 8px 14px;"
        );
        crystalLabel.setLayoutX(1100);
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

        ImageView icon = UILoader.createImageView(offer.icon_path, 0, 0, 55, 55);

        Label name = new Label(offer.name);
        name.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-weight: bold;");

        Label type = new Label(offer.type);
        type.setStyle("-fx-text-fill: #ff9b00; -fx-font-size: 10px; -fx-font-weight: bold;");

        Label price = new Label("\uD83D\uDC8E " + offer.cost + " CRYSTALS");
        price.setStyle("-fx-text-fill: #ff1e78; -fx-font-size: 11px; -fx-font-weight: bold;");

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
        final String icon_path;

        OperatorOffer(String id, String name, String type, int cost, String icon_path) {
            this.id = id;
            this.name = name;
            this.type = type;
            this.cost = cost;
            this.icon_path = icon_path;
        }
    }
}
