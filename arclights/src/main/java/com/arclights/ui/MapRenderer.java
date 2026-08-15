package com.arclights.ui;

import com.arclights.models.GameMap;
import com.arclights.models.MapConfig;
import com.arclights.models.Tile;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MapRenderer {

    public static class RenderResult {
        public double tileWidth;
        public double tileHeight;
        public double paddingX;
        public double paddingY;
        public double offsetX;
        public double offsetY;

        public RenderResult(double tileWidth, double tileHeight, double paddingX, double paddingY, double offsetX, double offsetY) {
            this.tileWidth = tileWidth;
            this.tileHeight = tileHeight;
            this.paddingX = paddingX;
            this.paddingY = paddingY;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }
    }

    public static RenderResult renderMap(Pane root, GameMap gameMap, double windowWidth, double windowHeight) {
        MapConfig config = gameMap.getMapConfig();
        double paddingX = config.getPaddingX();
        double paddingY = config.getPaddingY();
        double tileWidth;
        double tileHeight;
        double offsetX;
        double offsetY = config.getOffsetY();

        if (config.isAutoCalculateTileSize()) {
            double tileSizeW = (windowWidth - 100) / gameMap.getCols();
            double tileSizeH = (windowHeight - 100) / gameMap.getRows();
            tileWidth = tileSizeW;
            tileHeight = tileSizeH;
        } else {
            tileWidth = config.getTileWidth();
            tileHeight = config.getTileHeight();
        }

        if (config.getOffsetX() == 0) {
            double mapPixelWidth = gameMap.getCols() * (tileWidth + paddingX);
            offsetX = (windowWidth - mapPixelWidth) / 2.0;
        } else {
            offsetX = config.getOffsetX();
        }

        ImageView background = UILoader.createImageView(config.getBackgroundImagePath());
        if (background != null) {
            background.setFitWidth(windowWidth);
            background.setFitHeight(windowHeight);
            background.setPreserveRatio(false);
            root.getChildren().add(background);
        }


        return new RenderResult(tileWidth, tileHeight, paddingX, paddingY, offsetX, offsetY);
    }

    private static void drawInteractiveGrid(Pane root, GameMap gameMap, double tileWidth, double tileHeight, double paddingX, double paddingY, double offsetX, double offsetY) {
        for (int row = 0; row < gameMap.getRows(); row++) {
            for (int col = 0; col < gameMap.getCols(); col++) {
                Rectangle tileNode = new Rectangle(tileWidth, tileHeight);
                Tile logicTile = gameMap.getTile(row, col);

                // Default semi-transparent overlay to match background art
                tileNode.setFill(Color.TRANSPARENT);
                tileNode.setStroke(Color.rgb(255, 255, 255, 0.12));

                if (logicTile.getTileType() == Tile.TileType.ENEMY_SPAWN) {
                    tileNode.setFill(Color.rgb(255, 0, 0, 0.2));
                } else if (logicTile.getTileType() == Tile.TileType.PLAYER_OBJECTIVE) {
                    tileNode.setFill(Color.rgb(0, 120, 255, 0.2));
                }

                tileNode.setX(col * (tileWidth + paddingX) + offsetX);
                tileNode.setY(row * (tileHeight + paddingY) + offsetY);
                root.getChildren().add(tileNode);
            }
        }
    }
}