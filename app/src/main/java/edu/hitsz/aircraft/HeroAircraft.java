package edu.hitsz.aircraft;

import java.util.LinkedList;
import java.util.List;

import edu.hitsz.ImageManager;
import edu.hitsz.activity.MainActivity;
import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.HeroBullet;
import edu.hitsz.bullet.HeroBulletFactory;
import edu.hitsz.shootStrategy.DisperseStrategy;
import edu.hitsz.shootStrategy.StraightShootStrategy;

/**
 * 英雄飞机，游戏玩家操控，遵循单例模式（singleton)
 * 【单例模式】
 * @author hitsz
 */
public class HeroAircraft extends AbstractAircraft {

/*
        volatile 修饰，
        singleton = new Singleton() 可以拆解为3步：
        1、分配对象内存(给singleton分配内存)
        2、调用构造器方法，执行初始化（调用 Singleton 的构造函数来初始化成员变量）。
        3、将对象引用赋值给变量(执行完这步 singleton 就为非 null 了)。
        若发生重排序，假设 A 线程执行了 1 和 3 ，还没有执行 2，B 线程来到判断 NULL，B 线程就会直接返回还没初始化的 instance 了。
        volatile 可以避免重排序。
    */
    /** 英雄机对象单例 */
    private volatile static HeroAircraft instance = null;

    /**
     * 单例模式：私有化构造方法
     */
    private HeroAircraft() {
        super(MainActivity.screenWidth / 2., MainActivity.screenHeight - ImageManager.HERO_IMAGE.getHeight(),
                0, -5, 1000, new StraightShootStrategy(-1, new HeroBulletFactory(20)));
    }


    /**
     * 通过单例模式获得初始化英雄机
     * 【单例模式：双重校验锁方法】
     * @return 英雄机单例
     */
    public static HeroAircraft getInstance(){
        if (instance == null) {
            synchronized (HeroAircraft.class) {
                if (instance == null) {
                    instance = new HeroAircraft();
                }
            }
        }
        return instance;
    }

    public static void resetInstance() {
        instance = null;
    }

    @Override
    public void forward() {
        // 英雄机由touch控制，不通过forward函数移动
    }


    /* 攻击方式 */

    /**
     * 子弹一次发射数量
     */
    private int shootNum = 1;

    /**
     * 子弹射击方向 (向下发射：1，向上发射：-1)
     */
    private final int direction = -1;
    private long promotionTS = 0;

    public void increaseShootNum() {
        if(shootNum == 1) {
            // 从单发直射升级到三连发散射
            shootNum = 3;
        } else {
            shootNum += 1;
        }
        this.setShootStrategy(new DisperseStrategy(direction, new HeroBulletFactory(20), shootNum));
    }

    public void setPromotionTS(long promotionTS) {
        this.promotionTS = promotionTS;
    }
    public long getPromotionTS() {
        return promotionTS;
    }

//    @Override
//    public  List<BaseBullet> shoot() {
//        List<BaseBullet> res = new LinkedList<>();
//        double x = this.getLocationX();
//        double y = this.getLocationY() + direction*2;
//        int speedX = 0;
//        double speedY = this.getSpeedY() + direction*5;
//        BaseBullet baseBullet;
//        for(int i=0; i<shootNum; i++){
//            // 子弹发射位置相对飞机位置向前偏移
//            // 多个子弹横向分散
//            baseBullet = new HeroBullet(x + (i*2 - shootNum + 1)*10, y, speedX, speedY, power);
//            res.add(baseBullet);
//        }
//        return res;
//    }
}
