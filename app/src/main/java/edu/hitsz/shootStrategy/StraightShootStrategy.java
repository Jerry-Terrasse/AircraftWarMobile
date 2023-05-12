package edu.hitsz.shootStrategy;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.BaseBulletFactory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class StraightShootStrategy extends BaseShootStrategy {
    public StraightShootStrategy(int direction, BaseBulletFactory bulletFactory) {
        super(direction, bulletFactory);
    }
    @Override
    public List<BaseBullet> shoot(double x, double y, double vx, double vy) {
        List<BaseBullet> bullets = new LinkedList<>();
        int bulletVx = 0;
        double bulletVy = direction * velocity;
        bullets.add(bulletFactory.createBullet(x, y, bulletVx, bulletVy));
        return bullets;
    }
}
