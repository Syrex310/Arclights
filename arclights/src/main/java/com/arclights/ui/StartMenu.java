package com.arclights.ui;

import java.io.InputStream;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

public class StartMenu {

    public interface MenuCallbacks {
        void onTerminalClick();
        void onOperatorsClick();
        void onExitClick();
    }

    public static Scene createScene(MenuCallbacks callbacks) {
        Pane root = new Pane();
        root.setPrefSize(1280, 720);

        // Background loading
        ImageView bgView = null;
        try {
            InputStream bgStream = StartMenu.class.getResourceAsStream("/com/arclights/ref.png");
            if (bgStream != null) {
                Image bgImage = new Image(bgStream);
                bgView = new ImageView(bgImage);
                bgView.setFitWidth(1280);
                bgView.setFitHeight(720);
            }
        } catch (Exception ignored) {}

        if (bgView != null) {
            root.getChildren().add(bgView);
        } else {
            Rectangle fallbackBg = new Rectangle(800, 650);
            fallbackBg.setFill(new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#0d0f12")),
                new Stop(1, Color.web("#1c2026"))
            ));
            root.getChildren().add(fallbackBg);
        }

        // Tint
        Rectangle tint = new Rectangle(800, 650);
        tint.setFill(Color.rgb(10, 12, 15, 0.4));
        root.getChildren().add(tint);

        // Info Panel
        VBox infoPanel = new VBox(15);
        infoPanel.setLayoutX(60);
        infoPanel.setLayoutY(160);

        Label titleLabel = new Label("A R C L I G H T S");
        titleLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 42px; -fx-font-family: 'Arial'; -fx-font-weight: 900;");
        
        DropShadow ds = new DropShadow();
        ds.setOffsetY(3.0f);
        ds.setColor(Color.color(0.0f, 0.0f, 0.0f, 0.6f));
        titleLabel.setEffect(ds);

        Label subtitleLabel = new Label("TACTICAL DEFENSE PROTOCOL // PRTS-v1.2");
        subtitleLabel.setStyle("-fx-text-fill: #ff9b00; -fx-font-size: 11px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");

        Rectangle decBar = new Rectangle(200, 3, Color.web("#ff9b00"));

        VBox statusBox = new VBox(5);
        statusBox.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 10px; -fx-background-radius: 4px; -fx-border-color: rgba(255,255,255,0.1);");
        
        Label status1 = new Label("SYSTEM STATUS: ONLINE");
        status1.setStyle("-fx-text-fill: #28a745; -fx-font-size: 11px; -fx-font-family: 'monospace'; -fx-font-weight: bold;");
        Label status2 = new Label("LINK STABILITY: 98.4%");
        status2.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 11px; -fx-font-family: 'monospace';");
        Label status3 = new Label("SQUAD DATA SYNCED");
        status3.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 11px; -fx-font-family: 'monospace';");
        Label status4 = new Label("WARNING: INCOMING THREAT DETECTED");
        status4.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 11px; -fx-font-family: 'monospace'; -fx-font-weight: bold;");

        statusBox.getChildren().addAll(status1, status2, status3, status4);
        infoPanel.getChildren().addAll(titleLabel, subtitleLabel, decBar, statusBox);
        root.getChildren().add(infoPanel);

        // Buttons
        VBox btnPanel = new VBox(20);
        btnPanel.setLayoutX(440);
        btnPanel.setLayoutY(180);

        Button terminalBtn = createMenuButton("TERMINAL", "CHOOSE OPERATION STAGE", "#ff9b00", 0);
        terminalBtn.setOnAction(e -> callbacks.onTerminalClick());

        Button operatorsBtn = createMenuButton("OPERATORS", "SQUAD ARCHIVES [DEV]", "#cccccc", 20);
        operatorsBtn.setOnAction(e -> callbacks.onOperatorsClick());

        Button exitBtn = createMenuButton("EXIT SYSTEM", "TERMINATE LINK", "#dc3545", 40);
        exitBtn.setOnAction(e -> callbacks.onExitClick());

        btnPanel.getChildren().addAll(terminalBtn, operatorsBtn, exitBtn);
        root.getChildren().add(btnPanel);

        return new Scene(root, 800, 650);
    }

    private static Button createMenuButton(String mainText, String subText, String accentColorHex, double translateX) {
        Button btn = new Button();
        btn.setPrefSize(280, 65);
        btn.setTranslateX(translateX);

        VBox textContainer = new VBox(3);
        textContainer.setAlignment(Pos.CENTER_LEFT);
        
        Label mainLbl = new Label(mainText);
        mainLbl.setStyle("-fx-text-fill: inherit; -fx-font-size: 18px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
        Label subLbl = new Label(subText);
        subLbl.setStyle("-fx-text-fill: inherit; -fx-font-size: 9px; -fx-font-family: 'Arial'; -fx-opacity: 0.7; -fx-font-weight: bold;");
        
        textContainer.getChildren().addAll(mainLbl, subLbl);
        btn.setGraphic(textContainer);

        String baseStyle = 
            "-fx-background-color: rgba(20, 22, 25, 0.85); -fx-text-fill: #ffffff; -fx-border-color: " + accentColorHex + 
            "; -fx-border-width: 0 0 0 6px; -fx-alignment: center-left; -fx-padding: 10px 20px; -fx-background-radius: 4px; -fx-cursor: hand;";
            
        String hoverStyle = 
            "-fx-background-color: " + accentColorHex + "; -fx-text-fill: " + 
            (accentColorHex.equals("#cccccc") || accentColorHex.equals("#ff9b00") ? "#000000" : "#ffffff") + 
            "; -fx-border-color: #ffffff; -fx-border-width: 0 0 0 6px; -fx-alignment: center-left; -fx-padding: 10px 20px; -fx-background-radius: 4px; -fx-cursor: hand;";

        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> {
            btn.setStyle(hoverStyle);
            btn.setTranslateX(translateX - 5);
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(baseStyle);
            btn.setTranslateX(translateX);
        });

        return btn;
    }
}