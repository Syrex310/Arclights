package com.arclights.ui;

import java.io.InputStream;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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

        ImageView bgView = null;
        try {
            InputStream bgStream = StartMenu.class.getResourceAsStream("/com/arclights/background.png");
            if (bgStream != null) {
                Image bgImage = new Image(bgStream);
                bgView = new ImageView(bgImage);
                bgView.setFitWidth(1280);
                bgView.setFitHeight(720);
            }
        } catch (Exception e) {
            System.out.println(e);
        }

        if (bgView != null) {
            root.getChildren().add(bgView);
        } else {
            Rectangle fallbackBg = new Rectangle(1280, 720);
            fallbackBg.setFill(new LinearGradient(
                0, 0, 1, 1, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#0d0f12")),
                new Stop(1, Color.web("#1c2026"))
            ));
            root.getChildren().add(fallbackBg);
        }

        // Tint
        Rectangle tint = new Rectangle(1280, 720);
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

        Label subtitleLabel = new Label("Arclights");
        subtitleLabel.setStyle("-fx-text-fill: #ff9b00; -fx-font-size: 11px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");

        Rectangle decBar = new Rectangle(200, 3, Color.web("#ff9b00"));

        VBox statusBox = new VBox(5);
        statusBox.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-padding: 10px; -fx-background-radius: 4px; -fx-border-color: rgba(255,255,255,0.1);");
        
        Label status1 = new Label("Terminal");
        status1.setStyle("-fx-text-fill: #28a745; -fx-font-size: 11px; -fx-font-family: 'monospace'; -fx-font-weight: bold;");
        Label status2 = new Label("Recruit");
        status2.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 11px; -fx-font-family: 'monospace';");
        Label status3 = new Label("Squad");
        status3.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 11px; -fx-font-family: 'monospace';");
        Label status4 = new Label("Base");
        status4.setStyle("-fx-text-fill: #dc3545; -fx-font-size: 11px; -fx-font-family: 'monospace'; -fx-font-weight: bold;");

        statusBox.getChildren().addAll(status1, status2, status3, status4);
        infoPanel.getChildren().addAll(titleLabel, subtitleLabel, decBar, statusBox);
        root.getChildren().add(infoPanel);

        // Buttons
        VBox btnPanel = new VBox(20);
        btnPanel.setLayoutX(850);
        btnPanel.setLayoutY(200);

        Button terminalBtn = createMenuButton("TERMINAL", "CHOOSE OPERATION STAGE", "#ff9b00", 0, "");
        terminalBtn.setOnAction(e -> callbacks.onTerminalClick());

        Button operatorsBtn = createMenuButton("OPERATORS", "SQUAD", "#cccccc", 0, "");
        operatorsBtn.setOnAction(e -> callbacks.onOperatorsClick());

        Button exitBtn = createMenuButton("EXIT", "EXIT GAME", "#dc3545", 0, "");
        exitBtn.setOnAction(e -> callbacks.onExitClick());

        btnPanel.getChildren().addAll(terminalBtn, operatorsBtn, exitBtn);
        root.getChildren().add(btnPanel);

        //Test terminal ver 2
        VBox btnTerminal = new VBox(20);
        btnTerminal.setLayoutX(700);
        btnTerminal.setLayoutY(100);

        Button terminalButton = createMenuButton("Test Terminal 2", "Sanity", "#ffffff", 0, 100, 500, "/com/arclights/ui/Menu/btn_battle.png");
        terminalButton.setOnAction(e -> callbacks.onTerminalClick());
        btnTerminal.getChildren().addAll(terminalButton);
        root.getChildren().add(btnTerminal);

        return new Scene(root, 1280, 720);
    }

    private static Button createMenuButton(String mainText, String subText, String accentColorHex, double translateX, String imagePath) {
        return createMenuButton(mainText, subText, accentColorHex, translateX, 65, 280, null);
    }

    private static Button createMenuButton(String mainText, String subText, String accentColorHex, double translateX, double height, double width, String imagePath) {
        Button btn = new Button();
        btn.setPrefSize(width, height);
        btn.setTranslateX(translateX);

        Label mainLbl = new Label(mainText);
        mainLbl.setStyle("-fx-text-fill: inherit; -fx-font-size: 18px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");
        
        Label subLbl = new Label(subText);
        subLbl.setStyle("-fx-text-fill: inherit; -fx-font-size: 9px; -fx-font-family: 'Arial'; -fx-opacity: 0.7; -fx-font-weight: bold;");

        VBox textContainer = new VBox(3, mainLbl, subLbl);
        textContainer.setAlignment(Pos.CENTER_LEFT);
        btn.setGraphic(textContainer);

        if (imagePath != null && !imagePath.isEmpty()) {
            try {
                InputStream imgStream = StartMenu.class.getResourceAsStream(imagePath);
                if (imgStream != null) {
                    ImageView imgView = new ImageView(new Image(imgStream));
                    imgView.setFitWidth(width);  // Adjust icon size as needed
                    imgView.setFitHeight(height);

                    // Combine icon and text side-by-side
                    HBox contentBox = new HBox(0, imgView, textContainer);
                    contentBox.setAlignment(Pos.CENTER_LEFT);
                    btn.setGraphic(contentBox);
                } else {
                    btn.setGraphic(textContainer);
                }
            } catch (Exception e) {
                System.err.println("Failed to load icon: " + imagePath);
                btn.setGraphic(textContainer);
            }
        } else {
            // No image requested, just set text
            btn.setGraphic(textContainer);
        }


        String hoverTextColor = (accentColorHex.equals("#cccccc") || accentColorHex.equals("#ff9b00")) ? "#000000" : "#ffffff";
        
        String commonStyles = "-fx-border-width: 0 0 0 6px; -fx-alignment: center-left; -fx-padding: 10px 20px; -fx-background-radius: 4px; -fx-cursor: hand;";
        String baseStyle = "-fx-background-color: rgba(20, 22, 25, 0.85); -fx-text-fill: #ffffff; -fx-border-color: " + accentColorHex + "; " + commonStyles;
        String hoverStyle = "-fx-background-color: " + accentColorHex + "; -fx-text-fill: " + hoverTextColor + "; -fx-border-color: #ffffff; " + commonStyles;

        btn.setStyle(baseStyle);
        btn.setOnMouseEntered(e -> {
            btn.setStyle(hoverStyle);
            btn.setTranslateX(translateX - 5);
            btn.setStyle("-fx-background-color: #ffb1b1; -fx-padding: 20;");
        });
        btn.setOnMouseExited(e -> {
            btn.setStyle(baseStyle);
            btn.setTranslateX(translateX);
            btn.setStyle("-fx-background-color: #ffffff; -fx-padding: 20;");
        });
        btn.setStyle("-fx-background-color: #ffffff; -fx-padding: 20;");
        return btn;
    }
}