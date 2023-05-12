package edu.hitsz.supply;

import edu.hitsz.activity.MainActivity;
import edu.hitsz.game.WorldHandle;
import edu.hitsz.basic.AbstractFlyingObject;

public abstract class BaseSupply extends AbstractFlyingObject {

    public BaseSupply(double locationX, double locationY, double speedX, double speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void forward() {
        super.forward();

        // 判定 y 轴出界
        if (speedY > 0 && locationY >= MainActivity.screenHeight ) {
            // 向下飞行出界
            vanish();
        } else if (speedY < 0 && locationY <= 0){
            // 向上飞行出界
            vanish();
        }
//        System.out.println(speedY);
    }

    public void takeEffect(WorldHandle world) {
        System.out.println(this.getClass().getName() + " active!");
    }
}
