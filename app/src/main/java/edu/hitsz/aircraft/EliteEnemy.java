package edu.hitsz.aircraft;

import edu.hitsz.bullet.EnemyBulletFactory;
import edu.hitsz.shootStrategy.StraightShootStrategy;
import edu.hitsz.supply.*;

import java.util.LinkedList;
import java.util.List;

public class EliteEnemy extends AbstractEnemy {
    private final int direction = 1;
    
    /**
     * 生成概率
     */
    static private final double PROBABILITY = 0.2;

    static public double getProbability() {
        return PROBABILITY;
    }

    public EliteEnemy(double locationX, double locationY, double speedX, double speedY, int hp) {
        super(locationX, locationY, speedX, speedY, hp, new StraightShootStrategy(1, new EnemyBulletFactory(20)));
        this.score = 20;
    }

    @Override
    public List<BaseSupply> produceSupply() {
        BaseSupply supply;
        double vx = speedX;
        if (Math.random() < 0.5) {
            vx += 3;
        } else {
            vx -= 3;
        }

        double p = Math.random();
        if (p < 0.3) { // 30%概率掉落火力道具
            supply = FireSupplyFactory.getInstance().createSupply(locationX, locationY, vx, speedY);
        } else if(p < 0.6) { // 30%概率掉落加血道具
            supply = HpSupplyFactory.getInstance().createSupply(locationX, locationY, vx, speedY);
        } else if(p < 0.9) { // 30%概率掉落炸弹道具
            supply = BombSupplyFactory.getInstance().createSupply(locationX, locationY, vx, speedY);
        } else { // 10%概率不掉落道具
            supply = null;
        }

        List<BaseSupply> supplyList = new LinkedList<>();
        if(supply != null) { // 若掉落道具
            supplyList.add(supply);
        }
        return supplyList;
    }
}
