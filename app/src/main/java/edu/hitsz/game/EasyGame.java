package edu.hitsz.game;

import android.content.Context;
import android.os.Handler;

import edu.hitsz.ImageManager;

public class EasyGame extends BaseGame{

    public EasyGame(Context context, Handler handler, boolean withMusic) {
        super(context, handler, withMusic);
        this.backGround = ImageManager.BACKGROUND1_IMAGE;
    }

}
