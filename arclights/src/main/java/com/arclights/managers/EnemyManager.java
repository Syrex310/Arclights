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
        // Build the movement checkpoints based on grid layout math
        enemyPath.add(new Point2D(calcX(3), calcY(1)));
        enemyPath.add(new Point2D(calcX(3), calcY(3)));
        enemyPath.add(new Point2D(calcX(7), calcY(3)));
    }

    public void spawnEnemy() {
        // Instantiate the logic enemy at the starting tile (0, 1)
        Enemy originiumSlug = new Enemy(calcX(0), calcY(1), 100, 10, 1.0, enemyPath);
        activeEnemies.add(originiumSlug);

        // Instantiate visual node representation
        Circle enemySprite = new Circle(15, Color.RED);
        
        // Bind the graphic circle directly to the entity properties 
        enemySprite.centerXProperty().bind(originiumSlug.xProperty());
        enemySprite.centerYProperty().bind(originiumSlug.yProperty());
        root.getChildren().add(enemySprite);

        // Add listener to automatically remove the node from canvas when dead
        originiumSlug.isAliveProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                root.getChildren().remove(enemySprite);
                System.out.println("Enemy defeated! Despawned asset.");
            }
        });
    }

    public void update() {
        // Clean up dead enemies before looping
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