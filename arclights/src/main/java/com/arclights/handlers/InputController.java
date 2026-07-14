package com.arclights.handlers;

import com.arclights.managers.DeploymentManager;
import com.arclights.models.GameMap;

import javafx.scene.layout.Pane;

public class InputController {

    private final DeploymentManager deploymentManager;
    private final GameMap gameMap;

    public InputController(DeploymentManager deploymentManager, GameMap gameMap) {
        this.deploymentManager = deploymentManager;
        this.gameMap = gameMap;
    }

    public void attachInputHandlers(Pane root, Pane sniperGroup, Pane defenderGroup) {
        // 1. Initial Press hooks on the deployment items
        sniperGroup.setOnMousePressed(event -> {
            if (deploymentManager.getCurrentState() == DeploymentManager.SelectionState.NONE) {
                deploymentManager.startDrag(DeploymentManager.SelectionState.DRAGGING_SNIPER);
            }
        });

        defenderGroup.setOnMousePressed(event -> {
            if (deploymentManager.getCurrentState() == DeploymentManager.SelectionState.NONE) {
                deploymentManager.startDrag(DeploymentManager.SelectionState.DRAGGING_DEFENDER);
            }
        });

        // Capture initial click point when player begins Phase 2 (Direction swipe)
        root.setOnMousePressed(event -> {
            if (deploymentManager.getCurrentState() == DeploymentManager.SelectionState.SELECTING_DIRECTION) {
                deploymentManager.setDirectionDragStart(event.getX(), event.getY());
            }
        });

        // 2. Continuous Drag processing
        root.setOnMouseDragged(event -> {
            DeploymentManager.SelectionState state = deploymentManager.getCurrentState();
            if (state == DeploymentManager.SelectionState.DRAGGING_SNIPER || state == DeploymentManager.SelectionState.DRAGGING_DEFENDER) {
                deploymentManager.updateDragPosition(event.getX(), event.getY(), gameMap);
            } else if (state == DeploymentManager.SelectionState.SELECTING_DIRECTION) {
                deploymentManager.handleDirectionDrag(event.getX(), event.getY());
            }
        });

        // 3. Release drop processing configurations
        root.setOnMouseReleased(event -> {
            DeploymentManager.SelectionState state = deploymentManager.getCurrentState();
            if (state == DeploymentManager.SelectionState.DRAGGING_SNIPER || state == DeploymentManager.SelectionState.DRAGGING_DEFENDER) {
                deploymentManager.handleRelease(event.getX(), event.getY(), gameMap);
            } else if (state == DeploymentManager.SelectionState.SELECTING_DIRECTION) {
                deploymentManager.confirmDeployment();
            }
        });
    }
}