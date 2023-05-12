package edu.hitsz.game;

import android.content.Context;
import android.os.Handler;

import edu.hitsz.ImageManager;

public class HardGame extends BaseGame{
    public HardGame(Context context, Handler handler, boolean withMusic) {
        super(context, handler, withMusic);
        this.backGround = ImageManager.BACKGROUND3_IMAGE;
    }
}
