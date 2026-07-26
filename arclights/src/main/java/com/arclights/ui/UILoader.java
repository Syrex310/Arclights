package com.arclights.ui;

import java.io.InputStream;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class UILoader {
    public static void loadBackground(Pane root, String resourcePath, Color fallbackColor) {
        ImageView bgView = null;
        try (InputStream bgStream = UILoader.class.getResourceAsStream(resourcePath)) {
            if (bgStream != null) {
                Image bgImage = new Image(bgStream);
                bgView = new ImageView(bgImage);
                bgView.setFitWidth(1280);
                bgView.setFitHeight(720);
                bgView.setPreserveRatio(false);
            }
        } catch (Exception ignored) {}

        if (bgView != null) {
            root.getChildren().add(bgView);
        } else {
            Rectangle fallbackBg = new Rectangle(1280, 720, fallbackColor);
            root.getChildren().add(fallbackBg);
        }
    }

    public static void addTintOverlay(Pane root, double opacity) {
        Rectangle tint = new Rectangle(1280, 720);
        tint.setFill(Color.rgb(10, 12, 15, opacity));
        root.getChildren().add(tint);
    }

    public static Button createBackButton(Runnable onBackAction) {
        Button backBtn = new Button("<- BACK TO MAIN MENU");
        String baseStyle = "-fx-background-color: transparent; -fx-text-fill: #cccccc; " +
                           "-fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: transparent; -fx-text-fill: #ff9b00; " +
                            "-fx-font-family: 'Arial'; -fx-font-weight: bold; -fx-font-size: 12px; -fx-cursor: hand;";

        backBtn.setStyle(baseStyle);
        backBtn.setLayoutX(40);
        backBtn.setLayoutY(40);
        
        backBtn.setOnMouseEntered(e -> backBtn.setStyle(hoverStyle));
        backBtn.setOnMouseExited(e -> backBtn.setStyle(baseStyle));
        backBtn.setOnAction(e -> onBackAction.run());

        return backBtn;
    }

    public static Button createImageButton(String imagePath, double translateX, double width, double height) {
        Button btn = new Button();
        btn.setPrefSize(width, height);
        btn.setTranslateX(translateX);

        try (InputStream imgStream = UILoader.class.getResourceAsStream(imagePath)) {
            if (imgStream != null) {
                ImageView imgView = new ImageView(new Image(imgStream));
                imgView.setFitWidth(width);
                imgView.setFitHeight(height);
                btn.setGraphic(imgView);
            }
        } catch (Exception e) {
            System.err.println("Failed to load image button: " + imagePath);
        }

        btn.setStyle("-fx-background-color: transparent; -fx-padding: 0; -fx-cursor: hand;");

        btn.setOnMouseEntered(e -> {
            btn.setOpacity(0.8);
            btn.setTranslateX(translateX - 5);
        });
        btn.setOnMouseExited(e -> {
            btn.setOpacity(1.0);
            btn.setTranslateX(translateX);
        });

        return btn;
    }

    public static void addPageHeader(Pane root, String titleText, String subtitleText) {
        Label pageTitle = new Label(titleText);
        pageTitle.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 26px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
        pageTitle.setLayoutX(40);
        pageTitle.setLayoutY(80);

        Label pageSubtitle = new Label(subtitleText);
        pageSubtitle.setStyle("-fx-text-fill: #ff9b00; -fx-font-size: 10px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
        pageSubtitle.setLayoutX(40);
        pageSubtitle.setLayoutY(115);

        root.getChildren().addAll(pageTitle, pageSubtitle);
    }
}