package com.arclights.ui;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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
        root.setPrefSize(UILoader.WINDOW_WIDTH, UILoader.WINDOW_HEIGHT);

        UILoader.loadBackground(root, "/com/arclights/background2.png", Color.web("#0d0f12"));
        UILoader.addTintOverlay(root, 0.40);
        
        //Character
        ImageView sakikoChar = UILoader.createImageView("/com/arclights/Togawa_Sakiko_Elite_2.png", -220, -340, 1440, 1280);
        root.getChildren().add(sakikoChar);

        //Left
        VBox newsTerminal = new VBox();
        newsTerminal.setLayoutX(20);
        newsTerminal.setLayoutY(500);

        ImageView newsImage = UILoader.createImageView("/com/arclights/ui/50_i10.png", 0, 0, 208, 120);

        newsTerminal.getChildren().addAll(newsImage);

        //Right
        VBox btnTerminal = new VBox();
        btnTerminal.setLayoutX(670);
        btnTerminal.setLayoutY(80);
        
        //Row 1
        Button terminalButton = UILoader.createImageButton("/com/arclights/ui/Menu/btn_battle_refined2.png", 0, 0, 600, 165);
        terminalButton.setOnAction(e -> callbacks.onTerminalClick());

        //Row 2
        Button squadButton = UILoader.createImageButton("/com/arclights/ui/Menu/btn_squad_refined.png", 0, 0, 270, 120);
        squadButton.setOnAction(e -> callbacks.onOperatorsClick());

        Button charButton = UILoader.createImageButton("/com/arclights/ui/Menu/btn_char_repo_refined.png", 0, 0, 270, 120);
        charButton.setOnAction(e -> callbacks.onOperatorsClick());

        //Row 3
        Button shopButton = UILoader.createImageButton("/com/arclights/ui/Menu/btn_shop.png", 75, 0, 210, 120);
        shopButton.setOnAction(e -> callbacks.onOperatorsClick());

        Button recruitButton = UILoader.createImageButton("/com/arclights/ui/Menu/btn_recruit_normal.png", 60, 0, 165, 120);
        recruitButton.setOnAction(e -> callbacks.onOperatorsClick());

        Button headhuntButton = UILoader.createImageButton("/com/arclights/ui/Menu/btn_recruit_advanced.png", 60, 0, 165, 120);
        headhuntButton.setOnAction(e -> callbacks.onOperatorsClick());

        ImageView recruitGroupButton = UILoader.createImageView("/com/arclights/ui/Menu/grp_recruit.png", -271, 7, 313, 40);

        //Row 4
        Button missionButton = UILoader.createImageButton("/com/arclights/ui/Menu/btn_mission.png", 50, 0, 220, 120);
        missionButton.setOnAction(e -> callbacks.onExitClick());

        Button buildingButton = UILoader.createImageButton("/com/arclights/ui/Menu/btn_building.png", 53, 0, 220, 120);
        buildingButton.setOnAction(e -> callbacks.onExitClick());

        Button inventoryButton = UILoader.createImageButton("/com/arclights/ui/Menu/btn_inventory.png", 41, -3, 105, 123);
        inventoryButton.setOnAction(e -> callbacks.onExitClick());

        //Row arrangment
        HBox rowTwo = new HBox(); 
        rowTwo.getChildren().addAll(squadButton, charButton);

        HBox rowThree = new HBox();
        rowThree.getChildren().addAll(shopButton, recruitButton, headhuntButton, recruitGroupButton);

        HBox rowFour = new HBox();
        rowFour.getChildren().addAll(missionButton, buildingButton, inventoryButton);

        Button exitButton = UILoader.createImageButton("/com/arclights/ui/Menu/btn_squad.png", 0, 0, 270, 120); // Temporary
        exitButton.setOnAction(e -> callbacks.onExitClick());

        btnTerminal.getChildren().addAll(terminalButton, rowTwo, rowThree, rowFour);
        root.getChildren().addAll(newsTerminal, btnTerminal);

        return new Scene(root, UILoader.WINDOW_WIDTH, UILoader.WINDOW_HEIGHT);
    }
}