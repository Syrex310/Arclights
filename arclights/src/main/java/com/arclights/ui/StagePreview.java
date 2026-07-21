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
import javafx.scene.shape.Rectangle;

public class StagePreview {

    public interface StageSelectCallbacks {
        void onBackToMenu();
        void onDeployStage(char[][] layout, String name);
    }

    public static Scene createScene(StageSelectCallbacks callbacks) {
        Pane root = new Pane();
        root.setPrefSize(800, 650);

        Rectangle bg = new Rectangle(800, 650, Color.web("#0d0f12"));
        Rectangle tint = new Rectangle(800, 650, Color.rgb(10, 12, 15, 0.8));
        root.getChildren().addAll(bg, tint);

        Button backBtn = new Button("<- BACK TO MAIN MENU");
        backBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #cccccc; -fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;");
        backBtn.setLayoutX(40);
        backBtn.setLayoutY(40);
        backBtn.setOnAction(e -> callbacks.onBackToMenu());
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

        Pane card1 = createStageCard("01-01", "TRAINING GROUND", "Standard operation layout for new doctor onboarding.", MapPresets.LEVEL_1, "#ff9b00", callbacks);
        Pane card2 = createStageCard("01-02", "NARROW PASSAGE", "Defensive tactics exercise in a constrained corridor.", MapPresets.LEVEL_2, "#00a2ff", callbacks);

        cardsBox.getChildren().addAll(card1, card2);
        root.getChildren().add(cardsBox);

        return new Scene(root, 800, 650);
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

        Button deployBtn = new Button("DEPLOY / 出击");
        deployBtn.setPrefSize(260, 45);
        deployBtn.setStyle("-fx-background-color: " + accentColor + "; -fx-text-fill: #000000; -fx-font-weight: bold; -fx-cursor: hand;");
        deployBtn.setOnAction(e -> callbacks.onDeployStage(layout, name));

        card.getChildren().addAll(header, descLbl, deployBtn);
        return card;
    }
}