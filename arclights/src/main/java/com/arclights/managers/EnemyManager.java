package com.arclights.managers;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import com.arclights.entity.enemy.Enemy;
import com.arclights.entity.enemy.EnemyType;
import com.arclights.models.GameMap;
import com.arclights.models.Tile;

import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;

public class EnemyManager {
    private final List<Enemy> activeEnemies = new ArrayList<>();
    private final List<Point2D> enemyPath = new ArrayList<>();
    private final Pane root;
    private final GameMap gameMap;
    private int spawnRow = 1;
    private int spawnCol = 0;

    private static class GridPoint {
        final int r, c;
        GridPoint(int r, int c) { this.r = r; this.c = c; }
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof GridPoint)) return false;
            GridPoint that = (GridPoint) o;
            return r == that.r && c == that.c;
        }
        @Override
        public int hashCode() {
            return java.util.Objects.hash(r, c);
        }
    }

    public EnemyManager(Pane root, GameMap gameMap) {
        this.root = root;
        this.gameMap = gameMap;
        initPath();
    }

    private void initPath() {
        int detectedSpawnRow = -1;
        int detectedSpawnCol = -1;
        int targetRow = -1;
        int targetCol = -1;

        for (int r = 0; r < gameMap.getRows(); r++) {
            for (int c = 0; c < gameMap.getCols(); c++) {
                Tile tile = gameMap.getTile(r, c);
                if (tile.getTileType() == Tile.TileType.ENEMY_SPAWN) {
                    detectedSpawnRow = r;
                    detectedSpawnCol = c;
                } else if (tile.getTileType() == Tile.TileType.PLAYER_OBJECTIVE) {
                    targetRow = r;
                    targetCol = c;
                }
            }
        }

        if (detectedSpawnRow != -1 && detectedSpawnCol != -1) {
            this.spawnRow = detectedSpawnRow;
            this.spawnCol = detectedSpawnCol;
        }

        if (spawnRow == -1 || spawnCol == -1 || targetRow == -1 || targetCol == -1) {
            this.spawnRow = 1;
            this.spawnCol = 0;
            targetRow = 3;
            targetCol = 7;
        }

        // BFS pathfinding from (spawnRow, spawnCol) to (targetRow, targetCol)
        Queue<GridPoint> queue = new LinkedList<>();
        Set<GridPoint> visited = new HashSet<>();
        Map<GridPoint, GridPoint> parentMap = new HashMap<>();

        GridPoint start = new GridPoint(spawnRow, spawnCol);
        GridPoint target = new GridPoint(targetRow, targetCol);

        queue.add(start);
        visited.add(start);

        boolean found = false;
        int[] dr = {-1, 1, 0, 0};
        int[] dc = {0, 0, -1, 1};

        while (!queue.isEmpty()) {
            GridPoint curr = queue.poll();
            if (curr.r == target.r && curr.c == target.c) {
                found = true;
                break;
            }

            for (int i = 0; i < 4; i++) {
                int nr = curr.r + dr[i];
                int nc = curr.c + dc[i];

                if (nr >= 0 && nr < gameMap.getRows() && nc >= 0 && nc < gameMap.getCols()) {
                    Tile tile = gameMap.getTile(nr, nc);
                    if (tile.isWalkableForEnemies()) {
                        GridPoint neighbor = new GridPoint(nr, nc);
                        if (!visited.contains(neighbor)) {
                            visited.add(neighbor);
                            parentMap.put(neighbor, curr);
                            queue.add(neighbor);
                        }
                    }
                }
            }
        }

        if (found) {
            List<Point2D> path = new ArrayList<>();
            GridPoint curr = target;
            while (curr != null) {
                path.add(0, new Point2D(calcX(curr.c), calcY(curr.r)));
                curr = parentMap.get(curr);
            }
            if (!path.isEmpty()) {
                path.remove(0); // Exclude starting point itself
            }
            enemyPath.addAll(path);
        }
    }

    public void spawnEnemy(EnemyType type) {
        Enemy enemy = new Enemy(
            calcX(spawnCol), 
            calcY(spawnRow), 
            type.getHp(), 
            type.getAtk(), 
            type.getSpeed(), 
            enemyPath
        );
        activeEnemies.add(enemy);

        // Visual sprite mapped to type values
        Circle enemySprite = new Circle(type.getRadius(), type.getColor());
        enemySprite.centerXProperty().bind(enemy.xProperty()); //[cite: 3]
        enemySprite.centerYProperty().bind(enemy.yProperty()); //[cite: 3]
        root.getChildren().add(enemySprite); //[cite: 3]

        // This listener will now trigger correctly because isAlive changes inside takeDamage()
        enemy.isAliveProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                root.getChildren().remove(enemySprite); // Remove the visual sprite[cite: 3]
            }
        });
    }

    public void update() {
        // Remove dead enemies from memory loop
        activeEnemies.removeIf(enemy -> !enemy.isAlive()); //[cite: 3]
        for (Enemy enemy : activeEnemies) {
            enemy.update(); //[cite: 3]
        }
    }

    public List<Enemy> getActiveEnemies() {
        return activeEnemies; //[cite: 3]
    }

    private double calcX(int col) { return col * 62 + 50 + 30; } //[cite: 3]
    private double calcY(int row) { return row * 62 + 50 + 30; } //[cite: 3]
}