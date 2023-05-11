package edu.hitsz.aircraft;



import java.util.List;

import edu.hitsz.basic.AbstractFlyingObject;
import edu.hitsz.bullet.AbstractBullet;

/**
 * 所有种类飞机的抽象父类：
 * 敌机（BOSS, ELITE, MOB），英雄飞机
 *
 * @author hitsz
 */
public abstract class AbstractAircraft extends AbstractFlyingObject {
    /**
     * 生命值
     */
    protected int maxHp;
    protected int hp;



    /** 攻击方式
     * shootNum: 子弹一次发射数量
     * power:子弹伤害
     * direction:子弹射击方向 (向上发射：1，向下发射：-1)
     * rate: 调节子弹移动速度
     * shootStrategy: 攻击策略     * shootStrategy: 攻击策略
     */
    protected int shootNum = 1;
    protected int power = 30;
    protected int direction = -1;
    protected double rate = 1.5;

    public AbstractAircraft(double locationX, double locationY, double speedX, double speedY, int hp) {
        super(locationX, locationY, speedX, speedY);
        this.hp = hp;
        this.maxHp = hp;
    }

    /**
     * 减少HP
     * 一般在飞机被攻击时调用，减少一部分HP
     *
     * @param decrease HP的减少量，应为非负值
     */
    public void decreaseHp(int decrease) {
        hp -= decrease;
        if (hp <= 0) {
            hp = 0;
            vanish();
        }
    }


    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
        if (hp > maxHp) {
            hp = maxHp;
        }
    }
    /**
     * 飞机射击方法，可射击对象必须实现
     * @return
     *  可射击对象需实现，返回子弹
     *  非可射击对象空实现，返回null
     */
    public abstract List<AbstractBullet> shoot();

}


