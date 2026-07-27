/*package com.arclights.ui;

import com.arclights.models.GameMap;
import com.arclights.models.Tile;

import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MapRenderer {

    public static class RenderResult {
        public final double tileSize;
        public final double padding;
        public final double offsetX;
        public final double offsetY;

        public RenderResult(double tileSize, double padding, double offsetX, double offsetY) {
            this.tileSize = tileSize;
            this.padding = padding;
            this.offsetX = offsetX;
            this.offsetY = offsetY;
        }
    }

    public static RenderResult renderMap(Pane root, GameMap gameMap, double windowWidth, double windowHeight) {
        int padding = 2;

        double tileSizeW = (windowWidth - 100) / gameMap.getCols();
        double tileSizeH = (windowHeight - 100) / gameMap.getRows();
        double tileSize = Math.min(tileSizeW, tileSizeH);

        double mapPixelWidth = gameMap.getCols() * (tileSize + padding);
        double offsetX = (windowWidth - mapPixelWidth) / 2;
        int offsetY = 50;

        drawColoredTiles(root, gameMap, tileSize, padding, offsetX, offsetY);

        ImageView background = UILoader.createImageView("/com/arclights/map1-1(8).png");
        background.setFitHeight(UILoader.WINDOW_HEIGHT);
        background.setFitWidth(UILoader.WINDOW_WIDTH);
        root.getChildren().add(background);

        drawInvisibleTiles(root, gameMap, tileSize, padding, offsetX, offsetY);

        return new RenderResult(tileSize, padding, offsetX, offsetY);
    }

    private static void drawColoredTiles(Pane root, GameMap gameMap, double tileSize, double padding, double offsetX, double offsetY) {
        for (int row = 0; row < gameMap.getRows(); row++) {
            for (int col = 0; col < gameMap.getCols(); col++) {
                Rectangle tileNode = new Rectangle(tileSize, tileSize);
                Tile logicTile = gameMap.getTile(row, col);

                if (logicTile.getTileType() == Tile.TileType.RANGED_HIGH_GROUND) {
                    tileNode.setFill(Color.DARKGRAY);
                } else if (logicTile.getTileType() == Tile.TileType.ENEMY_SPAWN) {
                    tileNode.setFill(Color.RED);
                } else if (logicTile.getTileType() == Tile.TileType.PLAYER_OBJECTIVE) {
                    tileNode.setFill(Color.BLUE);
                } else if (logicTile.getDeploymentType() == Tile.DeploymentType.MELEE_ONLY) {
                    tileNode.setFill(Color.LIGHTGRAY);
                } else {
                    tileNode.setFill(Color.LIGHTPINK);
                }

                tileNode.setX(col * (tileSize + padding) + offsetX);
                tileNode.setY(row * (tileSize + padding) + offsetY);
                root.getChildren().add(tileNode);
            }
        }
    }

    private static void drawInvisibleTiles(Pane root, GameMap gameMap, double tileSize, double padding, double offsetX, double offsetY) {
        for (int row = 0; row < gameMap.getRows(); row++) {
            for (int col = 0; col < gameMap.getCols(); col++) {
                Rectangle tileNode = new Rectangle(tileSize, tileSize);
                tileNode.setFill(Color.TRANSPARENT);
                tileNode.setStroke(Color.rgb(255, 255, 255, 0.15)); // debug grid lines

                tileNode.setX(col * (tileSize + padding) + offsetX);
                tileNode.setY(row * (tileSize + padding) + offsetY);
                root.getChildren().add(tileNode);
            }
        }
    }
}*/

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

        // 1. Determine Tile Size & Offsets based on MapConfig
        if (config.isAutoCalculateTileSize()) {
            double tileSizeW = (windowWidth - 100) / gameMap.getCols();
            double tileSizeH = (windowHeight - 100) / gameMap.getRows();
            tileWidth = tileSizeW;
            tileHeight = tileSizeH;
        } else {
            tileWidth = config.getTileWidth();
            tileHeight = config.getTileHeight();
        }

        // Auto-center horizontally if offsetX is set to 0
        if (config.getOffsetX() == 0) {
            double mapPixelWidth = gameMap.getCols() * (tileWidth + paddingX);
            offsetX = (windowWidth - mapPixelWidth) / 2.0;
        } else {
            offsetX = config.getOffsetX();
        }

        // 2. Render dynamic map background image first
        ImageView background = UILoader.createImageView(config.getBackgroundImagePath());
        if (background != null) {
            background.setFitWidth(windowWidth);
            background.setFitHeight(windowHeight);
            background.setPreserveRatio(false);
            root.getChildren().add(background);
        }

        // 3. Render grid tiles (e.g. interactive overlays & debug outlines)
        drawInteractiveGrid(root, gameMap, tileWidth, tileHeight, paddingX, paddingY, offsetX, offsetY);

        return new RenderResult(tileWidth, tileHeight, paddingX, paddingY, offsetX, offsetY);
    }

    private static void drawInteractiveGrid(Pane root, GameMap gameMap, double tileWidth, double tileHeight, double paddingX, double paddingY, double offsetX, double offsetY) {
        for (int row = 0; row < gameMap.getRows(); row++) {
            for (int col = 0; col < gameMap.getCols(); col++) {
                Rectangle tileNode = new Rectangle(tileWidth, tileHeight);
                Tile logicTile = gameMap.getTile(row, col);

                // Default semi-transparent overlay to match background art
                tileNode.setFill(Color.TRANSPARENT);
                tileNode.setStroke(Color.rgb(255, 255, 255, 0.12)); // Subdued grid outline

                // Highlights for special tiles (optional debug/visual cues)
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