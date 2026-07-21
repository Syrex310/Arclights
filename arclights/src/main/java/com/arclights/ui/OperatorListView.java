package com.arclights.ui;

import java.io.InputStream;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
        root.setPrefSize(800, 650);

        // Load background image
        ImageView bgView = null;
        try {
            InputStream bgStream = OperatorListView.class.getResourceAsStream("/com/arclights/background.png");
            if (bgStream != null) {
                Image bgImage = new Image(bgStream);
                bgView = new ImageView(bgImage);
                bgView.setFitWidth(800);
                bgView.setFitHeight(650);
                bgView.setPreserveRatio(false);
            }
        } catch (Exception ignored) {}

        if (bgView != null) {
            root.getChildren().add(bgView);
        } else {
            Rectangle fallbackBg = new Rectangle(800, 650, Color.web("#0d0f12"));
            root.getChildren().add(fallbackBg);
        }

        // Tint overlay
        Rectangle tint = new Rectangle(800, 650);
        tint.setFill(Color.rgb(10, 12, 15, 0.85));
        root.getChildren().add(tint);

        // Back Button
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
        backBtn.setOnAction(e -> callbacks.onBackToMenu());
        root.getChildren().add(backBtn);

        // Header Titles
        Label pageTitle = new Label("OPERATOR ARCHIVES / 干员档案");
        pageTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 26px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
        pageTitle.setLayoutX(40);
        pageTitle.setLayoutY(80);

        Label pageSubtitle = new Label("OPERATIONAL UNIT ARCHIVES AND SQUAD MANAGEMENT");
        pageSubtitle.setStyle("-fx-text-fill: #ff9b00; -fx-font-size: 10px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
        pageSubtitle.setLayoutX(40);
        pageSubtitle.setLayoutY(115);

        root.getChildren().addAll(pageTitle, pageSubtitle);

        // Content Box
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

        // Warning Banner
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

        // Class Profiles Section
        Label infoTitle = new Label("AVAILABLE CLASS PROFILES (CURRENT LEVEL LOG)");
        infoTitle.setStyle("-fx-text-fill: #ff9b00; -fx-font-size: 12px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
        
        HBox classProfiles = new HBox(30);
        classProfiles.setAlignment(Pos.CENTER);
        
        VBox sniperProfile = createClassProfile(
            "SNIPER", 
            "RANGED / PHYSICAL", 
            "High speed single-target physical sniper. Deployed on high ground tiles to target incoming enemy drones and lightweight leaders.", 
            Color.GREEN
        );
        VBox defenderProfile = createClassProfile(
            "DEFENDER", 
            "MELEE / DEFENSE", 
            "Heavy blocker unit. Deployed on melee road tiles to intercept and stall up to 3 enemy units simultaneously.", 
            Color.BLUE
        );
        
        classProfiles.getChildren().addAll(sniperProfile, defenderProfile);
        archiveBox.getChildren().addAll(infoTitle, classProfiles);
        root.getChildren().add(archiveBox);

        return new Scene(root, 800, 650);
    }

    private static VBox createClassProfile(String title, String type, String desc, Color color) {
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
}