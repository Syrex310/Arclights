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
 * Warms {@link SpriteAnimation}'s static frame cache for every sprite the
 * game can show, at every size it can be shown at, before the player ever
 * reaches the main menu.
 *
 * Without this, the first time a given operator/enemy shows up in a given
 * map there's a decode hitch while its PNG frames get read + resized from
 * disk (see SpriteAnimation.loadFramesFromDisk). Frames are cached by
 * "resource directory + target pixel size" (see SpriteAnimation.load), and
 * the three playable stages (Stage 1_1, 1_3 and 1_4 - see StagePreview)
 * each use their own fixed tile size (MapConfig), so the same
 * operator/enemy needs to be decoded once per map's tile size, not just
 * once overall. This preloader mirrors exactly the sizes DeploymentManager
 * and EnemyManager use at runtime (via {@link SpriteSizing}), so every
 * cache entry it warms is one the game will actually hit later.
 */
public final class AssetPreloader {

    private AssetPreloader() {
        // static-only utility class
    }

    /** One sprite (operator or enemy) combined with the on-screen size it needs decoding at. */
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

        // The three tile sizes actually used in-game: one per playable stage.
        // (Stage 1_2's config is still auto-calculated/unused - see MapPresets -
        // so it's intentionally left out here.)
        MapConfig[] playableMapConfigs = {
            MapPresets.STAGE_1_1.getConfig(),
            MapPresets.STAGE_1_3.getConfig(),
            MapPresets.STAGE_1_4.getConfig(),
        };

        // Operator ids come straight from the catalog's real factories so this
        // list can never drift out of sync with whatever operators actually exist.
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
     * Builds a Task that decodes every animation state (start/idle/walk/attack/death)
     * for every operator + enemy, at every map's sprite size, straight into
     * SpriteAnimation's static cache. Run this off the FX thread (see
     * LoadingScreen) - Image decoding doesn't touch the scene graph, so it's
     * safe there. Once it finishes, every battle it touched loads instantly,
     * with zero first-use decode hitches.
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
                        // The ticksPerFrame/loop args only affect playback, not what
                        // gets cached, so their values here don't matter - only the
                        // path + width/height (the cache key) do.
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
