package edu.hitsz.game;

import android.content.Context;
import android.os.Handler;

import edu.hitsz.ImageManager;

public class MediumGame extends BaseGame{
    public MediumGame(Context context, Handler handler, boolean withMusic) {
        super(context, handler, withMusic);
        this.backGround = ImageManager.BACKGROUND2_IMAGE;
    }
}
