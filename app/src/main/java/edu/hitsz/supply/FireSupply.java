package edu.hitsz.supply;

import edu.hitsz.game.WorldHandle;

public class FireSupply extends BaseSupply {
    public FireSupply(double x, double y, double speedX, double speedY) {
        super(x, y, speedX, speedY);
    }

    @Override
    public void takeEffect(WorldHandle world) {
        super.takeEffect(world);
        world.increaseHeroFire();
    }
}
