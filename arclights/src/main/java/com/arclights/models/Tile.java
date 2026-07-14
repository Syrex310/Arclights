package com.arclights.models;

public class Tile {
    
    public enum TileType {
        MELEE_GROUND,      
        RANGED_HIGH_GROUND, 
        ENEMY_SPAWN,       
        PLAYER_OBJECTIVE,  
        HOLE_PIT           
    }

    public enum DeploymentType {
        MELEE_ONLY,    
        RANGED_ONLY,   
        ANY,           
        NONE           
    }

    private final TileType tileType;
    private final DeploymentType deploymentType;
    private boolean isOccupied;

    public Tile(TileType tileType, DeploymentType deploymentType) {
        this.tileType = tileType;
        this.deploymentType = deploymentType;
        this.isOccupied = false;
    }

    public boolean canPlaceMelee() {
        if (isOccupied) return false;
        return deploymentType == DeploymentType.MELEE_ONLY || deploymentType == DeploymentType.ANY;
    }

    public boolean canPlaceRanged() {
        if (isOccupied) return false;
        return deploymentType == DeploymentType.RANGED_ONLY || deploymentType == DeploymentType.ANY;
    }

    public boolean isWalkableForEnemies() {
        return tileType == TileType.MELEE_GROUND 
            || tileType == TileType.ENEMY_SPAWN 
            || tileType == TileType.PLAYER_OBJECTIVE
            || tileType == TileType.HOLE_PIT;
    }

    public boolean isEnemyPath() {
        return tileType == TileType.ENEMY_SPAWN || tileType == TileType.PLAYER_OBJECTIVE;
    }

    public TileType getTileType() { return tileType; }
    public DeploymentType getDeploymentType() { return deploymentType; }
    public boolean isOccupied() { return isOccupied; }
    public void setOccupied(boolean occupied) { this.isOccupied = occupied; }
}