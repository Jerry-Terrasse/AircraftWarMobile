package edu.hitsz.bullet;

public class HeroBulletFactory extends BaseBulletFactory {
    public HeroBulletFactory(int power) {
        super(power);
    }

    @Override
    public BaseBullet createBullet(double locationX, double locationY, double speedX, double speedY) {
        return new HeroBullet(locationX, locationY, speedX, speedY, power);
    }
}