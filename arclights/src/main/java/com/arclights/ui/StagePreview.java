package com.arclights.ui;

import com.arclights.models.MapPresets;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class StagePreview {

    public interface StageSelectCallbacks {
        void onBackToMenu();
        void onDeployStage(char[][] layout, String name);
    }

    public static Scene createScene(StageSelectCallbacks callbacks) {
        Pane root = new Pane();
        root.setPrefSize(UILoader.WINDOW_WIDTH, UILoader.WINDOW_HEIGHT);

        // Standardized Utilities
        UILoader.loadBackground(root, "/com/arclights/background2.png", Color.web("#0d0f12"));
        UILoader.addTintOverlay(root, 0.80);
        UILoader.addPageHeader(root, "TERMINAL", "SELECT OPERATIONS");
        
        Button backBtn = UILoader.createBackButton(callbacks::onBackToMenu);
        root.getChildren().add(backBtn);

        HBox cardsBox = new HBox(40);
        cardsBox.setLayoutX(60);
        cardsBox.setLayoutY(160);
        cardsBox.setPrefWidth(680);
        cardsBox.setAlignment(Pos.CENTER);

        Pane card1 = createStageCard("1-1", "Starter", "Starter", MapPresets.STAGE_1_1.getLayout(), "#ff9b00", callbacks);
        //Pane card2 = createStageCard("01-02", "NARROW PASSAGE", "Defensive tactics exercise in a constrained corridor.", MapPresets.STAGE_1_2.getLayout(), "#00a2ff", callbacks);
        Pane card3 = createStageCard("1-2", "Crossroad", "Intermediate", MapPresets.STAGE_1_3.getLayout(), "#00ff55", callbacks);
        Pane card4 = createStageCard("1-3", "Infinite", "Hard", MapPresets.STAGE_1_4.getLayout(), "#0073ff", callbacks);

        cardsBox.getChildren().addAll(card1, card3, card4);
        root.getChildren().add(cardsBox);

        return new Scene(root, UILoader.WINDOW_WIDTH, UILoader.WINDOW_HEIGHT);
    }

    private static Pane createStageCard(String code, String name, String desc, char[][] layout, String accentColor, StageSelectCallbacks callbacks) {
        VBox card = new VBox(15);
        card.setPrefSize(310, 420);
        card.setStyle("-fx-background-color: rgba(20, 22, 25, 0.9); -fx-border-color: rgba(255, 255, 255, 0.1); -fx-border-width: 1px; -fx-border-radius: 6px; -fx-background-radius: 6px; -fx-padding: 25px;");

        HBox header = new HBox(10);
        Label codeLbl = new Label(code);
        codeLbl.setStyle("-fx-text-fill: " + accentColor + "; -fx-font-size: 32px; -fx-font-family: 'Arial'; -fx-font-weight: 900;");
        Label nameLbl = new Label(name);
        nameLbl.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
        header.getChildren().addAll(codeLbl, nameLbl);

        Label descLbl = new Label(desc);
        descLbl.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 11px; -fx-wrap-text: true;");
        descLbl.setPrefHeight(60);

        Button deployBtn = new Button("DEPLOY");
        deployBtn.setPrefSize(260, 45);
        deployBtn.setStyle("-fx-background-color: " + accentColor + "; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-cursor: hand;");
        deployBtn.setOnAction(e -> callbacks.onDeployStage(layout, name));

        card.getChildren().addAll(header, descLbl, deployBtn);
        return card;
    }
}