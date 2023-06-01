package edu.hitsz.game;

import android.content.Context;

import android.os.Handler;
import android.view.SurfaceHolder;

import androidx.annotation.NonNull;

import java.util.Optional;

import edu.hitsz.online.RemoteSession;

public class OnlineGame extends HardGame {
    protected Integer opponentScore = 0;
    final private Object opponentScoreLock = new Object();
    int last_sync_time = 0;
    public OnlineGame(Context context, Handler handler, boolean withMusic) {
        super(context, handler, withMusic);
    }

    @Override
    protected void postProcessAction() {
        super.postProcessAction();
        if(time - last_sync_time > 1000) {
            last_sync_time = time;
            new Thread(() -> {
                try {
                    Optional<Integer> new_oppo_score = RemoteSession.getInstance().syncScore(this.score);
                    if (new_oppo_score.isPresent()) {
                        synchronized (this.opponentScoreLock) {
                            this.opponentScore = new_oppo_score.get();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    @Override
    protected void paintScoreAndLife() {
        super.paintScoreAndLife();
        int oppo_score = 0;
        synchronized (this.opponentScoreLock) {
            oppo_score = this.opponentScore;
        }
        int x = 10;
        int y = 40 + 60*2;
        canvas.drawText("OPPONENT SCORE: " + oppo_score, x, y, mPaint);
    }

    @Override
    public void gameOver() {
        super.gameOver();
        new Thread(() -> {
            try {
                RemoteSession.getInstance().syncScore(this.score);
                RemoteSession.getInstance().finishGame();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
