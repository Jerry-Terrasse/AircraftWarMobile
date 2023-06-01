package edu.hitsz.game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import androidx.annotation.NonNull;

import java.util.LinkedList;
import java.util.List;

import edu.hitsz.ImageManager;
import edu.hitsz.MusicPlayer;
import edu.hitsz.activity.GameActivity;
import edu.hitsz.activity.MainActivity;
import edu.hitsz.activity.RankActivity;
import edu.hitsz.aircraft.AbstractAircraft;
import edu.hitsz.aircraft.AbstractEnemy;
import edu.hitsz.aircraft.BossEnemy;
import edu.hitsz.aircraft.BossEnemyFactory;
import edu.hitsz.aircraft.EliteEnemy;
import edu.hitsz.aircraft.EliteEnemyFactory;
import edu.hitsz.aircraft.EnemyFactory;
import edu.hitsz.aircraft.HeroAircraft;
import edu.hitsz.aircraft.MobEnemyFactory;
import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.supply.BaseSupply;
import edu.hitsz.supply.BombListener;
import edu.hitsz.supply.BombSupply;

/**
 * 游戏逻辑抽象基类，遵循模板模式，action() 为模板方法
 * 包括：游戏主面板绘制逻辑，游戏执行逻辑。
 * 子类需实现抽象方法，实现相应逻辑
 * @author hitsz
 */
public abstract class BaseGame extends SurfaceView implements SurfaceHolder.Callback, Runnable, EnemyVanishListener, BombListener {

    public static final String TAG = "BaseGame";
    boolean mbLoop = false; //控制绘画线程的标志位
    private SurfaceHolder mSurfaceHolder;
    protected Canvas canvas;  //绘图的画布
    protected Paint mPaint;
    private Handler handler;

    //点击屏幕位置
    float clickX = 0, clickY=0;

    private int backGroundTop = 0;

    /**
     * 背景图片缓存，可随难度改变
     */
    protected Bitmap backGround;



    /**
     * 时间间隔(ms)，控制刷新频率
     */
    private int timeInterval = 10;

    private final HeroAircraft heroAircraft;

    protected final List<AbstractEnemy> enemyAircrafts;

    private final List<BaseBullet> heroBullets;
    private final List<BaseBullet> enemyBullets;

    private final List<BaseSupply> supplies;
    private final List<BaseSupply> suppliesToBeAdd;

    private final EnemyFactory mobEnemyFactory, eliteEnemyFactory;

    private final WorldHandle world;

    protected int enemyMaxNumber = 5;

    private boolean gameOverFlag = false;
    protected int score = 0;
    protected int time = 0;


    /**
     * 周期（ms)
     * 控制英雄机射击周期，默认值设为简单模式
     */
    private int cycleDuration = 100;
    private int cycleTime = 0;

    private boolean withMusic = false;

    protected Context context;

    public BaseGame(Context context, Handler handler, boolean withMusic){
        super(context);
        this.context = context;
        this.handler = handler;
        mbLoop = true;
        mPaint = new Paint();  //设置画笔
        mSurfaceHolder = this.getHolder();
        mSurfaceHolder.addCallback(this);
        this.setFocusable(true);
        ImageManager.initImage(context);

        this.withMusic = withMusic;

        // 初始化英雄机
        HeroAircraft.resetInstance();
        heroAircraft = HeroAircraft.getInstance();
        heroAircraft.setHp(1000);

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        supplies = new LinkedList<>();
        suppliesToBeAdd = new LinkedList<>();

        mobEnemyFactory = MobEnemyFactory.getInstance();
        eliteEnemyFactory = EliteEnemyFactory.getInstance();

        world = new WorldHandle(heroAircraft, enemyAircrafts, heroBullets, enemyBullets, supplies);

        heroController();

        BombSupply.addBombListener(this);
    }
    /**
     * 游戏启动入口，执行游戏逻辑
     */
    public void action() {

        Runnable task = () -> {
            try {

                time += timeInterval;
//                int random_id = (int) (Math.random() * 1000);
//                Log.i(TAG, "action: " + random_id);

                // 周期性执行（控制频率）
                if (timeCountAndNewCycleJudge()) {
                    if (enemyAircrafts.size() < enemyMaxNumber) {
                        AbstractEnemy newEnemy;
                        double enemyX = Math.random() * (MainActivity.screenWidth - ImageManager.MOB_ENEMY_IMAGE.getWidth());
                        double enemyY = Math.random() * MainActivity.screenHeight * 0.05;
                        double enemyVx = 0;
                        double enemyVy = 5;

                        // 若满足Boss出现条件，则产生Boss
                        if(this.score > BossEnemy.getScoreThreshold() && BossEnemy.getCount() == 0 && Math.random() < BossEnemy.getProbability()) {
                            enemyY += 5;
                            enemyVx = (Math.random() < 0.5 ? 1 : -1) * 1;
                            newEnemy = BossEnemyFactory.getInstance().createAircraft(enemyX, enemyY, enemyVx, 0);

                            // play boss bgm
                            if(withMusic) {
                                MusicPlayer.setBGM(MusicPlayer.androidBossBgm);
                            }
                        } else if (Math.random() < EliteEnemy.getProbability()) {
                            newEnemy = eliteEnemyFactory.createAircraft(enemyX, enemyY, enemyVx, enemyVy);
                        } else {
                            newEnemy = mobEnemyFactory.createAircraft(enemyX, enemyY, enemyVx, enemyVy);
                        }
                        newEnemy.addEnemyVanishListener(this);
                        enemyAircrafts.add(newEnemy);
                    }
                    shootAction();
                }

                // 子弹移动
                bulletsMoveAction();
                // 补给移动
                suppliesMoveAction();
                // 飞机移动
                aircraftsMoveAction();

                // 撞击检测
                crashCheckAction();
                // 后处理
                postProcessAction();

//                Log.i(TAG, "end: " + random_id);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        };
//        new Thread(task).start();
        task.run(); // blocking call
    }

    public void heroController(){
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                clickX = motionEvent.getX();
                clickY = motionEvent.getY();

                // 防止超出边界
                if(!(clickX < 0) && !(clickX > MainActivity.screenWidth) && !(clickY < 0) && !(clickY > MainActivity.screenHeight)) {
                    // 防止移动过远
                    double X = heroAircraft.getLocationX(), Y = heroAircraft.getLocationY();
                    double allowedDis = MainActivity.screenWidth / 5.;
                    if((X-clickX)*(X-clickX) + (Y-clickY)*(Y-clickY) < allowedDis*allowedDis) {
                        heroAircraft.setLocation(clickX, clickY);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    private boolean timeCountAndNewCycleJudge() {
        cycleTime += timeInterval;
        if (cycleTime >= cycleDuration && cycleTime - timeInterval < cycleTime) {
            // 跨越到新的周期
            cycleTime %= cycleDuration;
            return true;
        } else {
            return false;
        }
    }

    private void shootAction() {
        for(AbstractEnemy enemyAircraft : enemyAircrafts) {
            enemyBullets.addAll(enemyAircraft.shoot());
        }
        // 英雄射击
        heroBullets.addAll(heroAircraft.shoot());
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }
    private void suppliesMoveAction() {
        for (BaseSupply supply : supplies) {
            supply.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractAircraft enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }


    /**
     * 碰撞检测：
     * 碰撞检测：
     * 1. 敌机攻击英雄
     * 2. 英雄攻击/撞击敌机
     * 3. 英雄获得补给
     */
    private void crashCheckAction(){
        // 敌机子弹攻击英雄
        for (BaseBullet bullet : enemyBullets) {
            if(bullet.notValid() || heroAircraft.notValid()) {
                continue;
            }
            if(heroAircraft.crash(bullet)) { // crashed
//                playBulletHitMusic();
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

        // 英雄子弹攻击敌机
        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractEnemy enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    // 已被其他子弹击毁的敌机，不再检测
                    // 避免多个子弹重复击毁同一敌机的判定
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    if(withMusic) {
                        playBulletHitMusic();
                    }
                    // 敌机撞击到英雄机子弹
                    // 敌机损失一定生命值
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                }
            }
        }

        // 英雄撞击敌机
        for (AbstractEnemy enemy: enemyAircrafts) {
            if(enemy.notValid() || heroAircraft.notValid()) {
                continue;
            }
            // 英雄机 与 敌机 相撞，均损毁
            if (enemy.crash(heroAircraft) || heroAircraft.crash(enemy)) {
                enemy.vanish();
                heroAircraft.decreaseHp(Integer.MAX_VALUE);
            }
        }

        // 我方获得道具，道具生效
        for (BaseSupply supply : supplies) {
            if(supply.notValid()) {
                continue;
            }
            if(heroAircraft.crash(supply)) { // take effect
                supply.takeEffect(world);
                supply.vanish();
            }
        }
    }

    public void onEnemyVanish(int increaseScore, boolean isBoss, List<BaseSupply> dropSupplies) {
        this.score += increaseScore;
        suppliesToBeAdd.addAll(dropSupplies);

        if(isBoss && withMusic) {
            MusicPlayer.setBGM(MusicPlayer.androidGameBgm);
        }
    }

    public void onBomb() {
        if(withMusic) {
            MusicPlayer.play(MusicPlayer.androidBombMusic);
        }
    }
    public boolean notValid() {
        return gameOverFlag;
    }


    /**
     * 后处理：
     * 1. 删除无效的子弹
     * 2. 删除无效的敌机
     * 3. 检查英雄机生存
     * <p>
     * 无效的原因可能是撞击或者飞出边界
     */
    protected void postProcessAction() {
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        supplies.removeIf(AbstractFlyingObject::notValid);
        supplies.addAll(suppliesToBeAdd);
        suppliesToBeAdd.clear();

        if (heroAircraft.notValid()) {
            gameOver();
        }
    }

    public void gameOver() {
        gameOverFlag = true;
        mbLoop = false;
        Log.i(TAG, "Game Over!");

        if(withMusic) {
            MusicPlayer.onGameOver(MusicPlayer.androidGameOverMusic);
        }
//        if(withMusic) {
//            MusicThread musicThread = new MusicThread(MusicManager.getMusicPath("game_over"), false);
//            musicThread.start();
//
//            try {
//                bulletMusicThread.setStopped();
//                bulletMusicThread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            try {
//                bgmThread.setStopped();
//                bgmThread.join();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            if(bossBgmThread != null) {
//                try {
//                    bossBgmThread.setStopped();
//                    bossBgmThread.join();
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

//        recordDao.doAdd(new Record("user", score, -1));

//        List<Record> recordList = recordDao.getAll();
//        for(Record record: recordList) {
//            System.out.println(String.format("Id: %d\tName: %s\tScore: %d", record.getRecord_id(), record.getName(), record.getScore()));
//        }
//
//        RankTable rankTable = new RankTable(recordDao);
//        Main.cardPanel.add(rankTable.getMainPanel(), "rankTable");
//        Main.cardLayout.last(Main.cardPanel);

//        System.exit(0);
    }


    public void draw() {
        canvas = mSurfaceHolder.lockCanvas();
//        Log.i(TAG, "lockCanvas");
        if(mSurfaceHolder == null || canvas == null){
            return;
        }

        //绘制背景，图片滚动
        canvas.drawBitmap(backGround,0,this.backGroundTop-backGround.getHeight(),mPaint);
        canvas.drawBitmap(backGround,0,this.backGroundTop,mPaint);
        canvas.drawBitmap(backGround,0,this.backGroundTop + backGround.getHeight(),mPaint);
        backGroundTop +=1;
        if (backGroundTop == MainActivity.screenHeight)
            this.backGroundTop = 0;

        //先绘制子弹，后绘制飞机
        paintImageWithPositionRevised(enemyBullets); //敌机子弹
        paintImageWithPositionRevised(heroBullets);  //英雄机子弹

        paintImageWithPositionRevised(enemyAircrafts);//敌机

        paintImageWithPositionRevised(supplies); //补给


        canvas.drawBitmap(ImageManager.HERO_IMAGE,
                (int)heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                (int)heroAircraft.getLocationY()- ImageManager.HERO_IMAGE.getHeight() / 2,
                mPaint);

        //画生命值
        paintScoreAndLife();

        mSurfaceHolder.unlockCanvasAndPost(canvas);
//        Log.i(TAG, "unlockCanvasAndPost");
    }

    private void paintImageWithPositionRevised(List<? extends AbstractFlyingObject> objects) {
        if (objects.size() == 0) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            Bitmap image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image! ";
            canvas.drawBitmap(image, (int)object.getLocationX() - image.getWidth() / 2,
                    (int)object.getLocationY() - image.getHeight() / 2, mPaint);
        }
    }

    protected void paintScoreAndLife() {
        int x = 10;
        int y = 40;

        mPaint.setColor(Color.RED);
        mPaint.setTextSize(50);

        canvas.drawText("SCORE:" + this.score, x, y, mPaint);
        y = y + 60;
        canvas.drawText("LIFE:" + this.heroAircraft.getHp(), x, y, mPaint);
    }
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {
        new Thread(this).start();
        Log.i(TAG, "start surface view thread");
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        MainActivity.screenWidth = i1;
        MainActivity.screenHeight = i2;
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        mbLoop = false;
    }

    //***********************
    //      Music 各部分
    //***********************

    public void setWithMusic(boolean withMusic) {
        this.withMusic = withMusic;
        world.setWithMusic(withMusic);
    }
    public boolean getWithMusic() {
        return this.withMusic;
    }
    void playBulletHitMusic() {
        if(withMusic) {
            MusicPlayer.play(MusicPlayer.androidBulletHitMusic);
        }
    }


    @Override
    public void run() {

        Log.i(TAG, "withMusic: " + withMusic);
        if(withMusic) {
            MusicPlayer.init(context);
            MusicPlayer.setBGM(MusicPlayer.androidGameBgm);
            MusicPlayer.play(MusicPlayer.androidBulletShootMusic, true);
        }

//        Runnable r = () -> {
//            while (mbLoop) {
//                action();
//                try {
//                    Thread.sleep(timeInterval);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        Thread actionThread = new Thread(r);
//        actionThread.start();

        while (mbLoop){   //游戏结束停止绘制
            synchronized (mSurfaceHolder){
                try {
                    action();
                    draw();
                    try {
                        Thread.sleep(timeInterval);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } catch(Throwable t) {
                    t.printStackTrace();
                }
            }
        }

//        if(actionThread.isAlive()) {
//            try {
//                actionThread.join();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }

        Message message = Message.obtain();
        message.what = 1 ;
        message.arg1 = this.score;
        Log.i(TAG, "score: " + this.score);
//        Log.i(TAG, "Game address: " + BaseGame.this);
        handler.sendMessage(message);
    }
}
