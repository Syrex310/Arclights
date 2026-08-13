package com.arclights.ui;

import com.arclights.entity.operator.Operator;
import com.arclights.entity.operator.skill.OperatorSkill;
import com.arclights.entity.operator.skill.SkillActivationType;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/** Small in-operation panel used to select an operator and activate its skill. */
public class OperatorSkillPanel {
    private final VBox root;
    private final Label operatorLabel;
    private final Label skillLabel;
    private final Label spLabel;
    private final Button skillButton;
    private Operator selectedOperator;

    public OperatorSkillPanel() {
        root = new VBox(4);
        root.setLayoutX(25);
        root.setLayoutY(500);
        root.setPrefWidth(250);
        root.setStyle(
            "-fx-background-color: rgba(20, 24, 30, 0.94);" +
            "-fx-padding: 8px;" +
            "-fx-border-color: #666666;" +
            "-fx-border-width: 1px;"
        );

        operatorLabel = new Label("No operator selected");
        operatorLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        skillLabel = new Label("Skill: -");
        skillLabel.setStyle("-fx-text-fill: #ffcc66;");

        spLabel = new Label("SP: -");
        spLabel.setStyle("-fx-text-fill: #cccccc;");

        skillButton = new Button("ACTIVATE SKILL");
        skillButton.setMaxWidth(Double.MAX_VALUE);
        skillButton.setOnAction(e -> {
            if (selectedOperator != null) {
                selectedOperator.activateSkill();
                refresh();
            }
        });

        root.getChildren().addAll(operatorLabel, skillLabel, spLabel, skillButton);
    }

    public VBox getRoot() {
        return root;
    }

    public void selectOperator(Operator operator) {
        selectedOperator = operator;
        refresh();
    }

    public void clearSelection() {
        selectedOperator = null;
        refresh();
    }

    public void refresh() {
        if (selectedOperator == null || !selectedOperator.isAlive()) {
            operatorLabel.setText("No operator selected");
            skillLabel.setText("Skill: -");
            spLabel.setText("SP: -");
            skillButton.setDisable(true);
            return;
        }

        OperatorSkill skill = selectedOperator.getSkill();
        if (skill == null) {
            operatorLabel.setText(selectedOperator.getClass().getSimpleName());
            skillLabel.setText("Skill: None");
            spLabel.setText("SP: -");
            skillButton.setDisable(true);
            return;
        }

        operatorLabel.setText(selectedOperator.getClass().getSimpleName());
        skillLabel.setText("Skill: " + skill.getName());

        if (skill.isActive()) {
            if (skill.getActivationType() == SkillActivationType.DURATION) {
                spLabel.setText(String.format("ACTIVE — %.1fs remaining", skill.getRemainingDurationSeconds()));
            } else {
                spLabel.setText("READY — next attack empowered");
            }
        } else {
            spLabel.setText("SP: " + skill.getCurrentSP() + " / " + skill.getMaxSP());
        }

        skillButton.setDisable(!skill.isReady() || skill.isActive());
    }
}
