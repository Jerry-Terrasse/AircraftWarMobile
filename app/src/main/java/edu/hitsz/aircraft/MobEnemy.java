package edu.hitsz.aircraft;

import edu.hitsz.activity.MainActivity;
import edu.hitsz.shootStrategy.NoShootStrategy;

/**
 * 普通敌机
 * 不可射击
 *
 * @author hitsz
 */
public class MobEnemy extends AbstractEnemy {

    public MobEnemy(double locationX, double locationY, double speedX, double speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp, new NoShootStrategy());
        this.score = 10;
    }

    @Override
    public void forward() {
        super.forward();
        // 判定 y 轴向下飞行出界
        if (locationY >= MainActivity.screenHeight) {
            vanish();
        }
    }

}
