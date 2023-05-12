package edu.hitsz.shootStrategy;

import edu.hitsz.bullet.BaseBullet;
import edu.hitsz.bullet.BaseBulletFactory;

import java.util.LinkedList;
import java.util.List;

public class DisperseStrategy extends BaseShootStrategy {
    private int shootNum;
    public DisperseStrategy(int direction, BaseBulletFactory bulletFactory, int shootNum) {
        super(direction, bulletFactory);
        this.shootNum = shootNum;
    }
    @Override
    public List<BaseBullet> shoot(double x, double y, double vx, double vy) {
        List<BaseBullet> bullets = new LinkedList<>();
        if(shootNum % 2 == 0) {
            int sideNum = shootNum / 2;
            double angle = Math.PI / 2 / (shootNum - 1);
            for(int i=1; i <= sideNum; ++i) {
                double bulletVx = vx + Math.sin(angle * (i - 0.5)) * velocity;
                double bulletVy = vy + direction * Math.cos(angle * (i - 0.5)) * velocity;
                bullets.add(bulletFactory.createBullet(x, y, bulletVx, bulletVy));
                bulletVx = vx - Math.sin(angle * (i - 0.5)) * velocity;
                bullets.add(bulletFactory.createBullet(x, y, bulletVx, bulletVy));
            }
        } else {
            int sideNum = (shootNum-1) / 2;
            // center
            bullets.add(bulletFactory.createBullet(x, y, vx, vy + direction * velocity));
            double angle = Math.PI / 4 / sideNum;
            for(int i = 1; i <= sideNum; i++) {
                double bulletVx = vx + Math.sin(angle * i) * velocity;
                double bulletVy = vy + direction * Math.cos(angle * i) * velocity;
                bullets.add(bulletFactory.createBullet(x, y, bulletVx, bulletVy));
                bulletVx = vx - Math.sin(angle * i) * velocity;
                bullets.add(bulletFactory.createBullet(x, y, bulletVx, bulletVy));
            }
        }
        return bullets;
    }
}