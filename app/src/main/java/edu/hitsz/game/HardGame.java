package edu.hitsz.game;

import android.content.Context;
import android.os.Handler;

import edu.hitsz.ImageManager;

public class HardGame extends BaseGame{
    public HardGame(Context context, Handler handler) {
        super(context,handler);
        this.backGround = ImageManager.BACKGROUND3_IMAGE;
    }
}
