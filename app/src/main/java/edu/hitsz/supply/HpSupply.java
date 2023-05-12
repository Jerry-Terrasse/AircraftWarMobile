package edu.hitsz.supply;

//import edu.hitsz.application.MusicManager;
//import edu.hitsz.application.MusicThread;
import edu.hitsz.game.WorldHandle;

public class HpSupply extends BaseSupply {
    private int increase;

    public HpSupply(double x, double y, double speedX, double speedY, int increase) {
        super(x, y, speedX, speedY);
        this.increase = increase;
    }

    @Override
    public void takeEffect(WorldHandle world) {
        super.takeEffect(world);
        world.increaseHeroHp(increase);

//        MusicThread musicThread = new MusicThread(MusicManager.getMusicPath("get_supply"), false);
//        musicThread.start();
    }
}
