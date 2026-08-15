package com.arclights.models;

public class MapConfig {
    private String backgroundImagePath;
    private double tileWidth;
    private double tileHeight;
    private double offsetX;
    private double offsetY;
    private double paddingX;
    private double paddingY;
    private boolean autoCalculateTileSize;

    public MapConfig(String backgroundImagePath, double tileWidth, double tileHeight, double offsetX, double offsetY, double paddingX, double paddingY) {
        this.backgroundImagePath = backgroundImagePath;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.paddingX = paddingX;
        this.paddingY = paddingY;
        this.autoCalculateTileSize = false;
    }

    public MapConfig(String backgroundImagePath, double offsetX, double offsetY, double paddingX, double paddingY) {
        this.backgroundImagePath = backgroundImagePath;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.paddingX = paddingX;
        this.paddingY = paddingY;
        this.autoCalculateTileSize = true;
    }

    public static MapConfig defaultConfig(String bgPath) {
        return new MapConfig(bgPath, 0, 50, 2, 0); // 0 offsetX = auto-center horizontally
    }

    public String getBackgroundImagePath() { return backgroundImagePath; }
    public double getTileWidth() { return tileWidth; }
    public double getTileHeight() { return tileHeight; }
    public double getOffsetX() { return offsetX; }
    public double getOffsetY() { return offsetY; }
    public double getPaddingX() { return paddingX; }
    public double getPaddingY() { return paddingY; }
    public boolean isAutoCalculateTileSize() { return autoCalculateTileSize; }
}