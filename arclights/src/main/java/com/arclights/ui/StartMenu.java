package com.arclights.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class StartMenu {

    public interface MenuCallbacks {
        void onTerminalClick();
        void onOperatorsClick();
        void onExitClick();
    }

    public static Scene createScene(MenuCallbacks callbacks) {
        Pane root = new Pane();
        root.setPrefSize(1280, 720);

        // Standardized Utilities
        UILoader.loadBackground(root, "/com/arclights/background2.png", Color.web("#0d0f12"));
        UILoader.addTintOverlay(root, 0.40);

        // Image Buttons Container
        VBox btnTerminal = new VBox(10);
        btnTerminal.setLayoutX(660);
        btnTerminal.setLayoutY(100);

        Button terminalButton = UILoader.createImageButton("/com/arclights/ui/Menu/btn_battle_refined2.png", 0, 600, 165);
        terminalButton.setOnAction(e -> callbacks.onTerminalClick());

        Button squadButton = UILoader.createImageButton("/com/arclights/ui/Menu/btn_squad_refined.png", 0, 270, 120);
        squadButton.setOnAction(e -> callbacks.onOperatorsClick());

        Button exitButton = UILoader.createImageButton("/com/arclights/ui/Menu/btn_squad.png", 0, 270, 120);
        exitButton.setOnAction(e -> callbacks.onExitClick());

        btnTerminal.getChildren().addAll(terminalButton, squadButton, exitButton);
        root.getChildren().add(btnTerminal);

        return new Scene(root, 1280, 720);
    }
}