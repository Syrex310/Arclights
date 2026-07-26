package com.arclights.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class OperatorListView {

    public interface OperatorArchiveCallbacks {
        void onBackToMenu();
    }

    public static Scene createScene(OperatorArchiveCallbacks callbacks) {
        Pane root = new Pane();
        root.setPrefSize(1280, 720);

        // Standardized Utilities
        UILoader.loadBackground(root, "/com/arclights/background.png", Color.web("#0d0f12"));
        UILoader.addTintOverlay(root, 0.85);
        UILoader.addPageHeader(root, "OPERATOR ARCHIVES", "Test");
        
        Button backBtn = UILoader.createBackButton(callbacks::onBackToMenu);
        root.getChildren().add(backBtn);

        // Content Box
        VBox archiveBox = new VBox(20);
        archiveBox.setLayoutX(60);
        archiveBox.setLayoutY(160);
        archiveBox.setPrefSize(680, 420);
        archiveBox.setStyle(
            "-fx-background-color: rgba(10, 12, 15, 0.95); " +
            "-fx-border-color: rgba(255, 255, 255, 0.05); " +
            "-fx-border-width: 1px; -fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; -fx-padding: 30px;"
        );

        Label infoTitle = new Label("AVAILABLE OPERATOR");
        infoTitle.setStyle("-fx-text-fill: #ff9b00; -fx-font-size: 12px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
        
        HBox classProfiles = new HBox(30);
        classProfiles.setAlignment(Pos.CENTER);
        
        VBox sniperProfile = createClassProfile("SNIPER", "RANGED / PHYSICAL", "High speed single-target physical sniper.", Color.GREEN);
        VBox defenderProfile = createClassProfile("DEFENDER", "MELEE / DEFENSE", "Heavy blocker unit.", Color.BLUE);
        
        classProfiles.getChildren().addAll(sniperProfile, defenderProfile);
        archiveBox.getChildren().addAll(infoTitle, classProfiles);
        root.getChildren().add(archiveBox);

        return new Scene(root, 1280, 720);
    }

    private static VBox createClassProfile(String title, String type, String desc, Color color) {
        VBox profile = new VBox(10);
        profile.setPrefWidth(290);
        profile.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.02); -fx-border-color: rgba(255, 255, 255, 0.1); " +
            "-fx-border-width: 1px; -fx-background-radius: 4px; -fx-border-radius: 4px; -fx-padding: 15px;"
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
}