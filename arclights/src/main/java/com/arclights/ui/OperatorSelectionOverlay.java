package com.arclights.ui;

import com.arclights.audio.SoundManager;
import com.arclights.entity.operator.Operator;
import com.arclights.entity.operator.skill.OperatorSkill;
import com.arclights.models.OperatorCatalog;

import javafx.beans.binding.Bindings;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

/**
 * Arknights-style "operator selected" UI.
 */
public class OperatorSelectionOverlay {

    private static final double SQUARE_SIZE = 26;
    private static final double PORTRAIT_SIZE = 190;

    private final Group overlayContainer = new Group();

    // --- Diamond + retreat/skill squares, anchored to the operator ---
    private final Group indicatorGroup = new Group();
    private final Polygon diamond = new Polygon();
    private final StackPane retreatSquare = new StackPane();
    private final StackPane skillSquare = new StackPane();
    private final Rectangle skillFill = new Rectangle();
    private final Label skillSpLabel = new Label();

    // --- Bottom-left stat panel, fixed on screen ---
    private final Pane statPanel = new Pane();
    private final ImageView portraitView = new ImageView();
    private final Rectangle portraitFallback = new Rectangle(PORTRAIT_SIZE, PORTRAIT_SIZE);
    private final Label portraitFallbackLabel = new Label();
    private final Label nameLabel = new Label();
    private final Label hpLabel = new Label();
    private final Label atkLabel = new Label();
    private final Label defLabel = new Label();
    private final Label resLabel = new Label();
    private final Label intervalLabel = new Label();
    private final Label blockLabel = new Label();

    private Operator selectedOperator;
    private Runnable onRetreat;

    public OperatorSelectionOverlay(Pane root, double screenHeight) {
        buildDiamond();
        buildRetreatSquare();
        buildSkillSquare();
        indicatorGroup.getChildren().addAll(diamond, retreatSquare, skillSquare);

        buildStatPanel(screenHeight);

        overlayContainer.getChildren().addAll(indicatorGroup, statPanel);
        overlayContainer.setVisible(false);
        overlayContainer.setMouseTransparent(false);
        root.getChildren().add(overlayContainer);
    }

    private void buildDiamond() {
        diamond.setFill(Color.rgb(140, 220, 255, 0.10));
        diamond.setStroke(Color.rgb(220, 240, 255, 0.75));
        diamond.setStrokeWidth(1.6);
        diamond.setMouseTransparent(true);
    }

    private void buildRetreatSquare() {
        retreatSquare.setPrefSize(SQUARE_SIZE, SQUARE_SIZE);
        retreatSquare.setMaxSize(SQUARE_SIZE, SQUARE_SIZE);
        retreatSquare.setStyle(
            "-fx-background-color: rgba(40, 10, 10, 0.85);" +
            "-fx-border-color: rgba(255, 120, 120, 0.9);" +
            "-fx-border-width: 1.5px;" +
            "-fx-cursor: hand;"
        );

        Polygon arrow = new Polygon(
            -5.0, -4.0,
             5.0, -4.0,
             0.0,  6.0
        );
        arrow.setFill(Color.rgb(255, 210, 210, 0.95));
        arrow.setMouseTransparent(true);

        retreatSquare.getChildren().add(arrow);
        retreatSquare.setOnMouseClicked(event -> {
            if (onRetreat != null) onRetreat.run();
            event.consume();
        });
        retreatSquare.setOnMouseEntered(e -> retreatSquare.setOpacity(0.8));
        retreatSquare.setOnMouseExited(e -> retreatSquare.setOpacity(1.0));
    }

    private void buildSkillSquare() {
        skillSquare.setPrefSize(SQUARE_SIZE, SQUARE_SIZE);
        skillSquare.setMaxSize(SQUARE_SIZE, SQUARE_SIZE);
        skillSquare.setStyle(
            "-fx-background-color: rgba(15, 15, 20, 0.85);" +
            "-fx-border-color: rgba(255, 210, 110, 0.9);" +
            "-fx-border-width: 1.5px;" +
            "-fx-cursor: hand;"
        );

        skillFill.setWidth(SQUARE_SIZE - 3);
        skillFill.setHeight(0);
        skillFill.setFill(Color.rgb(255, 200, 90, 0.75));
        skillFill.setMouseTransparent(true);
        StackPane.setAlignment(skillFill, Pos.BOTTOM_CENTER);

        skillSpLabel.setStyle("-fx-text-fill: white; -fx-font-size: 8px; -fx-font-weight: bold;");
        skillSpLabel.setMouseTransparent(true);

        skillSquare.getChildren().addAll(skillFill, skillSpLabel);
        skillSquare.setOnMouseClicked(event -> {
            if (selectedOperator != null) {
                OperatorSkill skill = selectedOperator.getSkill();
                
                if (skill != null && skill.isReady() && !skill.isActive()) {
                    boolean activated = selectedOperator.activateSkill();
                    if (activated) {
                        SoundManager.playSkillSound();
                    }
                    refresh();
                }
            }
            event.consume();
        });
        skillSquare.setOnMouseEntered(e -> skillSquare.setOpacity(0.85));
        skillSquare.setOnMouseExited(e -> skillSquare.setOpacity(1.0));
    }

    private void buildStatPanel(double screenHeight) {
        statPanel.setPrefSize(PORTRAIT_SIZE + 130, PORTRAIT_SIZE);
        statPanel.setMaxSize(PORTRAIT_SIZE + 130, PORTRAIT_SIZE);
        statPanel.setLayoutX(16);
        statPanel.setLayoutY(screenHeight - PORTRAIT_SIZE - 16);
        statPanel.setMouseTransparent(false);

        portraitView.setFitWidth(PORTRAIT_SIZE);
        portraitView.setFitHeight(PORTRAIT_SIZE);
        portraitView.setPreserveRatio(false);
        Rectangle clip = new Rectangle(PORTRAIT_SIZE, PORTRAIT_SIZE);
        portraitView.setClip(clip);

        portraitFallback.setFill(Color.rgb(35, 38, 44, 0.95));
        portraitFallback.setStroke(Color.rgb(90, 95, 105, 0.9));

        portraitFallbackLabel.setStyle("-fx-text-fill: #999999; -fx-font-size: 11px; -fx-font-weight: bold;");
        portraitFallbackLabel.setWrapText(true);
        portraitFallbackLabel.setMaxWidth(PORTRAIT_SIZE - 20);
        portraitFallbackLabel.setLayoutX(10);
        portraitFallbackLabel.setLayoutY(PORTRAIT_SIZE - 34);

        String statStyle = "-fx-text-fill: #f0f0f0; -fx-font-size: 11px; -fx-font-weight: bold;";
        nameLabel.setStyle("-fx-text-fill: #ffcc66; -fx-font-size: 12px; -fx-font-weight: bold;");
        hpLabel.setStyle(statStyle);
        atkLabel.setStyle(statStyle);
        defLabel.setStyle(statStyle);
        resLabel.setStyle(statStyle);
        intervalLabel.setStyle(statStyle);
        blockLabel.setStyle(statStyle);

        javafx.scene.layout.VBox statsBox = new javafx.scene.layout.VBox(2,
            nameLabel, hpLabel, atkLabel, defLabel, resLabel, intervalLabel, blockLabel);
        statsBox.setLayoutX(PORTRAIT_SIZE + 8);
        statsBox.setLayoutY(6);
        statsBox.setStyle(
            "-fx-background-color: rgba(10, 12, 16, 0.55);" +
            "-fx-padding: 6px 8px;" +
            "-fx-background-radius: 3px;"
        );

        statPanel.getChildren().addAll(portraitFallback, portraitView, portraitFallbackLabel, statsBox);
    }

    public void show(Operator operator, OperatorCatalog.Definition definition,
                      double tileWidth, double tileHeight, Runnable onRetreat) {
        this.selectedOperator = operator;
        this.onRetreat = onRetreat;

        layoutDiamond(tileWidth, tileHeight);
        indicatorGroup.translateXProperty().bind(operator.xProperty());
        indicatorGroup.translateYProperty().bind(operator.yProperty());

        boolean hasSkill = operator.getSkill() != null;
        skillSquare.setVisible(hasSkill);
        skillSquare.setManaged(hasSkill);

        bindStats(operator, definition);

        overlayContainer.setVisible(true);
        refresh();
    }

    public void hide() {
        overlayContainer.setVisible(false);
        indicatorGroup.translateXProperty().unbind();
        indicatorGroup.translateYProperty().unbind();
        selectedOperator = null;
        onRetreat = null;
    }

    public boolean isVisible() {
        return overlayContainer.isVisible();
    }

    public Operator getSelectedOperator() {
        return selectedOperator;
    }

    public boolean isPartOfOverlay(Node target) {
        Node n = target;
        while (n != null) {
            if (n == overlayContainer) return true;
            n = n.getParent();
        }
        return false;
    }

    public void refresh() {
        if (selectedOperator == null || !overlayContainer.isVisible()) return;

        if (!selectedOperator.isAlive()) {
            hide();
            return;
        }

        atkLabel.setText("ATK  " + (int) selectedOperator.getEffectiveAtk());
        defLabel.setText("DEF  " + (int) selectedOperator.getEffectiveDefense());

        OperatorSkill skill = selectedOperator.getSkill();
        if (skill != null) {
            double ratio = skill.isActive() ? 1.0 : Math.min(1.0, skill.getCurrentSP() / (double) skill.getMaxSP());
            skillFill.setHeight((SQUARE_SIZE - 3) * ratio);
            skillFill.setFill(skill.isReady() || skill.isActive()
                ? Color.rgb(255, 225, 130, 0.9)
                : Color.rgb(255, 200, 90, 0.55));
            skillSpLabel.setText(skill.isActive() ? "ON" : skill.getCurrentSP() + "/" + skill.getMaxSP());
        }
    }

    private void layoutDiamond(double tileWidth, double tileHeight) {
        double halfW = Math.max(24, tileWidth * 0.9);
        double halfH = Math.max(18, tileHeight * 0.58);

        diamond.getPoints().setAll(
            0.0, -halfH,   // top
            halfW, 0.0,    // right
            0.0, halfH,    // bottom
            -halfW, 0.0    // left
        );

        retreatSquare.setLayoutX(-halfW / 2.0 - SQUARE_SIZE / 2.0);
        retreatSquare.setLayoutY(-halfH / 2.0 - SQUARE_SIZE / 2.0);

        skillSquare.setLayoutX(halfW / 2.0 - SQUARE_SIZE / 2.0);
        skillSquare.setLayoutY(halfH / 2.0 - SQUARE_SIZE / 2.0);
    }

    private void bindStats(Operator operator, OperatorCatalog.Definition definition) {
        //cancel old binding
        atkLabel.textProperty().unbind();
        defLabel.textProperty().unbind();

        nameLabel.setText(definition != null ? definition.displayName : operator.getClass().getSimpleName());

        hpLabel.textProperty().bind(Bindings.createStringBinding(
            () -> "HP   " + (int) Math.ceil(operator.getHp()) + " / " + (int) operator.getMaxHp(),
            operator.hpProperty(), operator.maxHpProperty()));

        atkLabel.setText("ATK  " + (int) operator.getEffectiveAtk());
        defLabel.setText("DEF  " + (int) operator.getEffectiveDefense());

        resLabel.textProperty().bind(Bindings.createStringBinding(
            () -> "RES  " + (int) operator.getResistance() + "%",
            operator.resistanceProperty()));

        intervalLabel.textProperty().bind(Bindings.createStringBinding(
            () -> String.format("ATK SPD  %.2fs", operator.getAttackInterval() / OperatorSkill.TICKS_PER_SECOND),
            operator.attackIntervalProperty()));

        blockLabel.textProperty().bind(Bindings.createStringBinding(
            () -> "BLOCK  " + operator.getBlockCount(),
            operator.blockCountProperty()));

        Image portrait = definition != null ? UILoader.loadImage(definition.portraitPath) : null;
        if (portrait != null) {
            portraitView.setImage(portrait);
            portraitView.setVisible(true);
            portraitFallback.setVisible(false);
            portraitFallbackLabel.setVisible(false);
        } else {
            portraitView.setVisible(false);
            portraitFallback.setVisible(true);
            portraitFallbackLabel.setVisible(true);
            portraitFallbackLabel.setText(definition != null ? definition.displayName : operator.getClass().getSimpleName());
        }
    }
}