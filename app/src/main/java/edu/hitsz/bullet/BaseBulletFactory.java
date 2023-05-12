package edu.hitsz.bullet;

public abstract class BaseBulletFactory {
    protected int power;
    BaseBulletFactory(int power) {
        this.power = power;
    }
    public abstract BaseBullet createBullet(double locationX, double locationY, double speedX, double speedY);
}