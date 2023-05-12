package edu.hitsz;

import android.content.Context;
import android.media.MediaPlayer;

import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import edu.hitsz.R;


public class MusicPlayer {

    public static Optional<Context> context = Optional.empty();

    /* for android resource */
    public static final int androidGameOverMusic = R.raw.game_over;
    public static final int androidGameBgm = R.raw.bgm;
    public static final int androidBulletShootMusic = R.raw.bullet;
    public static final int androidBulletHitMusic = R.raw.bullet_hit;
    public static final int androidBossBgm = R.raw.bgm_boss;
    public static final int androidBombMusic = R.raw.bomb_explosion;
    public static final int androidGetSupplyMusic = R.raw.get_supply;

    private static final List<MediaPlayer> lockedMusicPlayers = new LinkedList<>();
    private static Optional<MediaPlayer> bgmPlayer = Optional.empty();
    private static Optional<MediaPlayer> bossBgmPlayer = Optional.empty();

    private static boolean mute = false;

    public static void init(Context context) {
        MusicPlayer.context = Optional.of(context);
    }

    public static boolean isMute() {
        return mute;
    }
    public static void setMute(boolean mute) {
        MusicPlayer.mute = mute;
    }
    public static void stopAll() {
        synchronized (lockedMusicPlayers) {
            for (MediaPlayer player : lockedMusicPlayers) {
                player.stop();
            }
            lockedMusicPlayers.clear();
        }
        bgmPlayer = Optional.empty();
        bossBgmPlayer = Optional.empty();
    }
    public static Optional<MediaPlayer> play(int url) {
        return play(url, false);
    }

    public static Optional<MediaPlayer> play(int url, boolean repeat) {
        if (mute) {
            return Optional.empty();
        }
        if (context.isPresent()) {

            MediaPlayer player = MediaPlayer.create(context.get(), url);

            synchronized (lockedMusicPlayers) {
                lockedMusicPlayers.add(player);
            }

            player.start();

            if (repeat) {
                player.setLooping(true);
            }

            player.setOnCompletionListener((mp) -> {
                synchronized (lockedMusicPlayers) {
                    player.release();
                    lockedMusicPlayers.remove(player);
                }
            });

            return Optional.of(player);
        } else {
            return Optional.empty();
        }

    }

    public static void setBGM(int url) {
        if (url == MusicPlayer.androidBossBgm) {
            bgmPlayer.ifPresent(MediaPlayer::pause);
            bossBgmPlayer = play(url, true);
        } else if (url == MusicPlayer.androidGameBgm) {
            bossBgmPlayer.ifPresent(MediaPlayer::stop);
            if (bgmPlayer.isPresent()) {
                bgmPlayer.get().start();
            } else {
                bgmPlayer = play(url, true);
            }
        }

    }

    public static void onGameOver(int url) {
        stopAll();
        play(url, false);
    }

}