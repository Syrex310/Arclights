package com.arclights.audio;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.Scene;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public final class SoundManager {
    private static String currentMusicFile;
    
    private static final String SOUND_PATH = "/sounds/";

    // =========================================================
    // Volume
    // =========================================================

    private static double musicVolume = 0.35;
    private static double sfxVolume = 0.65;

    // =========================================================
    // Background music
    // =========================================================

    private static MediaPlayer currentMusic;

    // Keep active SFX players alive until they finish.
    private static final List<MediaPlayer> activeSfx = new ArrayList<>();

    private SoundManager() {
    }

    // =========================================================
    // Background Music
    // =========================================================

    public static void playMenuMusic() {
        playMusic("bgm_menu.mp3");
    }

    public static void playBattleMusic() {
        playMusic("bgm_battle.mp3");
    }

    public static void playMusic(String fileName) {

        // Same music is already playing -> do nothing.
        if (currentMusic != null
                && currentMusicFile != null
                && currentMusicFile.equals(fileName)) {

            // Make sure it is actually playing.
            currentMusic.play();
            return;
        }

        stopMusic();

        URL resource = SoundManager.class.getResource(SOUND_PATH + fileName);

        if (resource == null) {
            System.err.println(
                "[SoundManager] Music not found: "
                + SOUND_PATH + fileName
            );
            return;
        }

        try {
            Media media = new Media(resource.toExternalForm());

            currentMusic = new MediaPlayer(media);
            currentMusicFile = fileName;

            currentMusic.setVolume(musicVolume);
            currentMusic.setCycleCount(MediaPlayer.INDEFINITE);

            currentMusic.setOnError(() ->
                System.err.println(
                    "[SoundManager] Music error: "
                    + currentMusic.getError()
                )
            );

            currentMusic.play();

        } catch (Exception e) {
            System.err.println(
                "[SoundManager] Failed to play music: "
                + fileName
            );

            currentMusic = null;
            currentMusicFile = null;

            e.printStackTrace();
        }
    }

    public static void stopMusic() {

        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
            currentMusic = null;
        }

        currentMusicFile = null;
    }

    public static void installMenuClickSound(Scene scene) {
        scene.addEventFilter(
            javafx.scene.input.MouseEvent.MOUSE_PRESSED,
            event -> {
                if (event.getTarget() instanceof javafx.scene.Node) {
                    javafx.scene.Node node = (javafx.scene.Node) event.getTarget();
                    
                    while (node != null) {
                        if (node instanceof javafx.scene.control.Button) {
                            playMenuClickSound();
                            break;
                        }
                        node = node.getParent();
                    }
                }
            }
        );
    }

    // =========================================================
    // Sound Effects
    // =========================================================

    public static void playDeploySound() {
        playSfx("sfx_deploy.wav");
    }

    public static void playOperatorDeathSound() {
        playSfx("sfx_operator_death.wav");
    }

    public static void playSkillSound() {
        playSfx("sfx_skill.wav");
    }

    public static void playMenuClickSound() {
        playSfx("sfx_menu_click.wav");
    }

    public static void playRetreatSound() {
        playSfx("sfx_retreat.wav");
    }

    public static void playStageClearSound() {
        playSfx("sfx_stage_clear.mp3");
    }

    public static void playDefeatSound() {
        playSfx("sfx_defeat.mp3");
    }

    /**
     * Plays a one-shot sound effect.
     *
     * Multiple SFX can play simultaneously.
     */
    public static void playSfx(String fileName) {

        URL resource = SoundManager.class.getResource(SOUND_PATH + fileName);

        if (resource == null) {
            System.err.println(
                "[SoundManager] SFX not found: "
                + SOUND_PATH + fileName
            );
            return;
        }

        try {
            Media media = new Media(resource.toExternalForm());
            MediaPlayer player = new MediaPlayer(media);

            player.setVolume(sfxVolume);

            activeSfx.add(player);

            player.setOnEndOfMedia(() -> {
                player.stop();
                player.dispose();
                activeSfx.remove(player);
            });

            player.setOnError(() -> {
                System.err.println(
                    "[SoundManager] SFX error: "
                    + player.getError()
                );

                player.dispose();
                activeSfx.remove(player);
            });

            player.play();

        } catch (Exception e) {
            System.err.println(
                "[SoundManager] Failed to play SFX: "
                + fileName
            );

            e.printStackTrace();
        }
    }

    // =========================================================
    // Volume
    // =========================================================

    public static void setMusicVolume(double volume) {
        musicVolume = clamp(volume);

        if (currentMusic != null) {
            currentMusic.setVolume(musicVolume);
        }
    }

    public static void setSfxVolume(double volume) {
        sfxVolume = clamp(volume);
    }

    public static double getMusicVolume() {
        return musicVolume;
    }

    public static double getSfxVolume() {
        return sfxVolume;
    }

    private static double clamp(double value) {
        return Math.max(0.0, Math.min(1.0, value));
    }

    // =========================================================
    // Shutdown
    // =========================================================

    public static void shutdown() {

        stopMusic();

        for (MediaPlayer player : new ArrayList<>(activeSfx)) {
            player.stop();
            player.dispose();
        }

        activeSfx.clear();
    }
}