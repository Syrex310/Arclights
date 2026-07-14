package com.arclights.managers;

import java.util.ArrayList;
import java.util.List;

import com.arclights.models.Enemy;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class EnemyManager {
    private final List<Enemy> activeEnemies = new ArrayList<>();
    private final List<Point2D> enemyPath = new ArrayList<>();
    private final Pane root;

    public EnemyManager(Pane root) {
        this.root = root;
        initPath();
    }

    private void initPath() {
        enemyPath.add(new Point2D(calcX(3), calcY(1)));
        enemyPath.add(new Point2D(calcX(3), calcY(3)));
        enemyPath.add(new Point2D(calcX(7), calcY(3)));
    }

    public void spawnEnemy() {
        Enemy originiumSlug = new Enemy(calcX(0), calcY(1), 100, 10, 1.0, enemyPath);
        activeEnemies.add(originiumSlug);

        Circle enemySprite = new Circle(15, Color.RED);
        enemySprite.centerXProperty().bind(originiumSlug.xProperty());
        enemySprite.centerYProperty().bind(originiumSlug.yProperty());
        root.getChildren().add(enemySprite);

        originiumSlug.isAliveProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                root.getChildren().remove(enemySprite);
            }
        });
    }

    public void update() {
        activeEnemies.removeIf(enemy -> !enemy.isAlive());
        for (Enemy enemy : activeEnemies) {
            enemy.update();
        }
    }

    public List<Enemy> getActiveEnemies() {
        return activeEnemies;
    }

    private double calcX(int col) { return col * 62 + 50 + 30; }
    private double calcY(int row) { return row * 62 + 50 + 30; }
}