package edu.hitsz.shootStrategy;

import edu.hitsz.bullet.BaseBullet;

import java.util.LinkedList;
import java.util.List;

public class NoShootStrategy extends BaseShootStrategy {
    public NoShootStrategy() {
        super(0, null);
    }

    @Override
    public List<BaseBullet> shoot(double x, double y, double vx, double vy) {
        return new LinkedList<>();
    }
}
