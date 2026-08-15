package com.arclights.animation;

import java.util.ArrayList;
import java.util.List;

import com.arclights.entity.enemy.EnemyType;
import com.arclights.entity.operator.Operator;
import com.arclights.models.MapConfig;
import com.arclights.models.MapPresets;
import com.arclights.models.OperatorCatalog;

import javafx.concurrent.Task;

/**
 * Prewarms sprite frames for each playable map size before the game starts.
 */
public final class AssetPreloader {

    private AssetPreloader() {
    }

    /** Describes one sprite to preload at a target on-screen size. */
    private static final class Job {
        final String spriteId;
        final String group;
        final boolean enemy;
        final double width;
        final double height;

        Job(String spriteId, String group, boolean enemy, double width, double height) {
            this.spriteId = spriteId;
            this.group = group;
            this.enemy = enemy;
            this.width = width;
            this.height = height;
        }
    }

    private static List<Job> buildJobs() {
        List<Job> jobs = new ArrayList<>();

        MapConfig[] playableMapConfigs = {
            MapPresets.STAGE_1_1.getConfig(),
            MapPresets.STAGE_1_3.getConfig(),
            MapPresets.STAGE_1_4.getConfig(),
        };

        List<String> operatorIds = new ArrayList<>();
        for (OperatorCatalog.Definition def : OperatorCatalog.all()) {
            Operator sample = def.create(0, 0);
            operatorIds.add(sample.getClass().getSimpleName().toLowerCase());
        }

        List<String> enemyIds = new ArrayList<>();
        for (EnemyType type : EnemyType.values()) {
            enemyIds.add(type.name().toLowerCase());
        }

        for (MapConfig config : playableMapConfigs) {
            double tileWidth = config.getTileWidth();
            double tileHeight = config.getTileHeight();
            double operatorSize = SpriteSizing.operatorSize(tileWidth, tileHeight);
            double enemySize = SpriteSizing.enemySize(tileWidth, tileHeight);

            for (String id : operatorIds) {
                jobs.add(new Job(id, "operators", false, operatorSize, operatorSize));
            }
            for (String id : enemyIds) {
                jobs.add(new Job(id, "enemies", true, enemySize, enemySize));
            }
        }

        return jobs;
    }

    /**
     * Builds a task that preloads all sprite frames for the game.
     */
    public static Task<Void> createPreloadTask() {
        return new Task<Void>() {
            @Override
            protected Void call() {
                List<Job> jobs = buildJobs();
                AnimationState[] states = AnimationState.values();
                int total = Math.max(1, jobs.size() * states.length);
                int done = 0;

                updateProgress(0, total);
                updateMessage("Preparing operation data...");

                for (Job job : jobs) {
                    for (AnimationState state : states) {
                        if (isCancelled()) {
                            return null;
                        }

                        String path = "/sprites/" + job.group + "/" + job.spriteId + "/" + state.name().toLowerCase();
                        SpriteAnimation.load(path, 1, false, job.width, job.height);

                        done++;
                        updateProgress(done, total);
                        updateMessage("Loading " + (job.enemy ? "enemy" : "operator") + ": " + job.spriteId);
                    }
                }

                updateMessage("Ready.");
                return null;
            }
        };
    }
}
