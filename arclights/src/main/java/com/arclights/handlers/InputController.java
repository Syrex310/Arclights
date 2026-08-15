package com.arclights.handlers;

import java.util.Map;

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

    /**
     * Wires up input handling for the battle scene.
     *
     * @param operatorCards operator id -> its deployment card node, built
     *                      dynamically from every operator the player
     *                      currently owns (see OperatorDeploymentBar).
     */
    public void attachInputHandlers(Pane root, Map<String, Pane> operatorCards) {
        for (Map.Entry<String, Pane> entry : operatorCards.entrySet()) {
            String operatorId = entry.getKey();
            Pane card = entry.getValue();
            card.setOnMousePressed(event -> {
                if (deploymentManager.getCurrentState() == DeploymentManager.SelectionState.NONE
                        && deploymentManager.canAfford(operatorId)) {
                    deploymentManager.startDrag(operatorId);
                }
            });
        }

        root.setOnMousePressed(event -> {
            if (deploymentManager.getCurrentState() == DeploymentManager.SelectionState.SELECTING_DIRECTION) {
                deploymentManager.setDirectionDragStart(event.getX(), event.getY());
            }
        });

        root.setOnMouseDragged(event -> {
            DeploymentManager.SelectionState state = deploymentManager.getCurrentState();
            if (state == DeploymentManager.SelectionState.DRAGGING) {
                deploymentManager.updateDragPosition(event.getX(), event.getY(), gameMap);
            } else if (state == DeploymentManager.SelectionState.SELECTING_DIRECTION) {
                deploymentManager.handleDirectionDrag(event.getX(), event.getY());
            }
        });

        root.setOnMouseReleased(event -> {
            DeploymentManager.SelectionState state = deploymentManager.getCurrentState();
            if (state == DeploymentManager.SelectionState.DRAGGING) {
                deploymentManager.handleRelease(event.getX(), event.getY(), gameMap);
            } else if (state == DeploymentManager.SelectionState.SELECTING_DIRECTION) {
                deploymentManager.confirmDeployment();
            }
        });
    }
}