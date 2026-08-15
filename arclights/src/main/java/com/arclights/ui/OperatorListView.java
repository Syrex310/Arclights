package com.arclights.ui;

import com.arclights.models.PlayerProgress;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

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
        "placeholder_operator_6",
        "placeholder_operator_7",
        "placeholder_operator_8"
    };

    private static final String[] OPERATOR_NAMES = {
        "Kroos", "Beagle", "Eyjafjalla",
        "Hibicus", "Blaze", "Projekt Red",
        "Fang", "Skadi"
    };

    private static final String[] OPERATOR_DESCRIPTIONS = {
        "Physical damage dealer, enhanced attack every 2 attacks",
        "High def and capable of blocking up to 3 enemies, 35SP for 35s of 80%+ Def",
        "Cast Arts Damage, Innate charge every 5s to deal another 2.7x Atk",
        "Heal allies, increased atk when activated",
        "All-around operator, 2 blocks, decent atk/def to hold lane",
        "Trade-off, Low DP cost and huge atk buff however have the least hp with no resistance/defense",
        "Low DP for early defense",
        "Support Allies"
    };

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

    public static Scene createScene(OperatorArchiveCallbacks callbacks) {
        Pane root = new Pane();
        root.setPrefSize(UILoader.WINDOW_WIDTH, UILoader.WINDOW_HEIGHT);

        UILoader.loadBackground(root, "/com/arclights/background2.png", Color.web("#0d0f12"));
        UILoader.addTintOverlay(root, 0.85);
        UILoader.addPageHeader(root, "OPERATOR ARCHIVES", "RECRUITED OPERATORS");

        Button backBtn = UILoader.createBackButton(callbacks::onBackToMenu);
        root.getChildren().add(backBtn);

        VBox archiveBox = new VBox(18);
        archiveBox.setLayoutX(60);
        archiveBox.setLayoutY(160);
        archiveBox.setPrefSize(1100, 420);
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
                operatorGrid.getChildren().add(createOperatorCard(OPERATOR_NAMES[i], i, OPERATOR_DESCRIPTIONS[i], OPERATOR_PORTRAITS[i]));
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

    private static VBox createOperatorCard(String name, int index, String description, String icon_path) {
        VBox card = new VBox(8);
        card.setPrefSize(250, 145);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(
            "-fx-background-color: rgba(255, 255, 255, 0.04); " +
            "-fx-border-color: rgba(255, 155, 0, 0.35); " +
            "-fx-border-width: 1px; -fx-background-radius: 4px; " +
            "-fx-border-radius: 4px; -fx-padding: 15px;"
        );

        //Rectangle icon = new Rectangle(42, 42, Color.web("#555b66"));
        ImageView icon = UILoader.createImageView(icon_path, 0, 0, 42, 42);
        Label title = new Label(name);
        title.setStyle("-fx-text-fill: #ffffff; -fx-font-size: 14px; -fx-font-family: 'Arial'; -fx-font-weight: bold;");

        Label details = new Label(description);
        details.setStyle("-fx-text-fill: #999999; -fx-font-size: 10px; -fx-font-family: 'Arial';");
        details.setWrapText(true);

        card.getChildren().addAll(icon, title, details);
        return card;
    }
}
