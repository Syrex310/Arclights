package com.arclights.ui;

import com.arclights.animation.AssetPreloader;

import javafx.beans.binding.Bindings;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * First screen the player sees. While it's showing, a background thread
 * decodes every operator/enemy sprite sheet - for all three playable maps'
 * tile sizes - straight into SpriteAnimation's cache (see AssetPreloader),
 * so no battle ever has to decode a sprite for the first time mid-fight.
 */
public final class LoadingScreen {

    private LoadingScreen() {
        // static-only utility class
    }

    public interface LoadCallbacks {
        void onLoadComplete();
    }

    public static Scene createScene(LoadCallbacks callbacks) {
        Pane root = new Pane();
        root.setPrefSize(UILoader.WINDOW_WIDTH, UILoader.WINDOW_HEIGHT);

        UILoader.loadBackground(root, "/com/arclights/background2.png", Color.web("#0d0f12"));
        UILoader.addTintOverlay(root, 0.55);

        Label title = new Label("ARCLIGHTS");
        title.setStyle(
            "-fx-text-fill: #ffffff; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-font-weight: 900; " +
            "-fx-font-size: 40px;"
        );

        Label statusLabel = new Label("Initializing...");
        statusLabel.setStyle(
            "-fx-text-fill: #ffffff; " +
            "-fx-font-family: 'Arial'; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 13px;"
        );

        Label percentLabel = new Label("0%");
        percentLabel.setStyle(
            "-fx-text-fill: #cccccc; " +
            "-fx-font-size: 11px; " +
            "-fx-font-weight: bold; " +
            "-fx-font-family: 'Arial';"
        );

        VBox loadingBox = new VBox(14, title, statusLabel, percentLabel);
        loadingBox.setAlignment(Pos.CENTER);
        loadingBox.setPrefWidth(UILoader.WINDOW_WIDTH);
        loadingBox.setLayoutY(UILoader.WINDOW_HEIGHT / 2.0 - 90);

        root.getChildren().add(loadingBox);

        Task<Void> preloadTask = AssetPreloader.createPreloadTask();
        statusLabel.textProperty().bind(preloadTask.messageProperty());
        percentLabel.textProperty().bind(Bindings.createStringBinding(
            () -> Math.round(Math.max(0, preloadTask.getProgress()) * 100) + "%",
            preloadTask.progressProperty()
        ));

        preloadTask.setOnSucceeded(e -> callbacks.onLoadComplete());
        preloadTask.setOnFailed(e -> {
            // Don't strand the player on the loading screen if a sprite is
            // missing/unreadable - fall back to the main menu and let the
            // normal in-game lazy loading (with its circle fallback) take over.
            Throwable ex = preloadTask.getException();
            if (ex != null) {
                ex.printStackTrace();
            }
            callbacks.onLoadComplete();
        });

        Thread loaderThread = new Thread(preloadTask, "asset-preloader");
        loaderThread.setDaemon(true);
        loaderThread.start();

        return new Scene(root, UILoader.WINDOW_WIDTH, UILoader.WINDOW_HEIGHT);
    }
}
