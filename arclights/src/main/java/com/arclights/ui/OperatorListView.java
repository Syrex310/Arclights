package com.arclights.ui;

import com.arclights.models.PlayerProgress;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class OperatorListView {

    public interface OperatorArchiveCallbacks {
        void onBackToMenu();
    }

    // These IDs/names are placeholders until real operator data is added.
    private static final String[] OPERATOR_IDS = {
        "placeholder_operator_1",
        "placeholder_operator_2",
        "placeholder_operator_3",
        "placeholder_operator_4",
        "placeholder_operator_5",
        "placeholder_operator_6"
    };

    private static final String[] OPERATOR_NAMES = {
        "PLACEHOLDER 01", "PLACEHOLDER 02", "PLACEHOLDER 03",
        "PLACEHOLDER 04", "PLACEHOLDER 05", "PLACEHOLDER 06"
    };

    public static Scene createScene(OperatorArchiveCallbacks callbacks) {
        Pane root = new Pane();
        root.setPrefSize(UILoader.WINDOW_WIDTH, UILoader.WINDOW_HEIGHT);

        UILoader.loadBackground(root, "/com/arclights/background.png", Color.web("#0d0f12"));
        UILoader.addTintOverlay(root, 0.85);
        UILoader.addPageHeader(root, "OPERATOR ARCHIVES", "RECRUITED OPERATORS");

        Button backBtn = UILoader.createBackButton(callbacks::onBackToMenu);
        root.getChildren().add(backBtn);

        VBox archiveBox = new VBox(18);
        archiveBox.setLayoutX(60);
        archiveBox.setLayoutY(160);
        archiveBox.setPrefSize(900, 420);
        archiveBox.setStyle(
            "-fx-background-color: rgba(10, 12, 15, 0.95); " +
            "-fx-border-color: rgba(255, 255, 255, 0.05); " +
            "-fx-border-width: 1px; -fx-border-radius: 6px; " +
            "-fx-background-radius: 6px; -fx-padding: 25px;"
        );

        Label infoTitle = new Label("RECRUITED OPERATORS");
        infoTitle.setStyle("-fx-text-fill: #ff9b00; -fx-font-size: 12px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");

        FlowPane operatorGrid = new FlowPane();
        operatorGrid.setHgap(15);
        operatorGrid.setVgap(15);
        operatorGrid.setPrefWrapLength(820);

        boolean hasOperators = false;
        for (int i = 0; i < OPERATOR_IDS.length; i++) {
            if (PlayerProgress.ownsOperator(OPERATOR_IDS[i])) {
                operatorGrid.getChildren().add(createOperatorCard(OPERATOR_NAMES[i], i));
                hasOperators = true;
            }
        }

        if (!hasOperators) {
            Label empty = new Label("No additional operators recruited yet.\nVisit the shop to recruit operators.");
            empty.setStyle("-fx-text-fill: #999999; -fx-font-size: 14px; -fx-font-family: 'Arial';");
            empty.setWrapText(true);
            operatorGrid.getChildren().add(empty);
        }

        archiveBox.getChildren().addAll(infoTitle, operatorGrid);
        root.getChildren().add(archiveBox);

        return new Scene(root, UILoader.WINDOW_WIDTH, UILoader.WINDOW_HEIGHT);
    }

    private static VBox createOperatorCard(String name, int index) {
        VBox card = new VBox(8);
        card.setPrefSize(250, 145);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.04); " +
            "-fx-border-color: rgba(255, 155, 0, 0.35); " +
            "-fx-border-width: 1px; -fx-background-radius: 4px; " +
            "-fx-border-radius: 4px; -fx-padding: 15px;"
        );

        Rectangle icon = new Rectangle(42, 42, Color.web("#555b66"));
        Label title = new Label(name);
        title.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");

        Label details = new Label("Placeholder operator #" + (index + 1) + "\nReady for future implementation.");
        details.setStyle("-fx-text-fill: #999999; -fx-font-size: 10px; -fx-font-family: 'Arial';");

        card.getChildren().addAll(icon, title, details);
        return card;
    }
}
