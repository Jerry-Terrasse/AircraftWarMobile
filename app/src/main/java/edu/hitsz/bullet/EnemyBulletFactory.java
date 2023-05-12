package edu.hitsz.bullet;

public class EnemyBulletFactory extends BaseBulletFactory {
    public EnemyBulletFactory(int power) {
        super(power);
    }

    @Override
    public BaseBullet createBullet(double locationX, double locationY, double speedX, double speedY) {
        return new EnemyBullet(locationX, locationY, speedX, speedY, power);
    }
}
