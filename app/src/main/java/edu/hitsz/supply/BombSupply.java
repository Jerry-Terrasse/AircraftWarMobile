package edu.hitsz.supply;

import edu.hitsz.game.WorldHandle;

import java.util.LinkedList;
import java.util.List;

public class BombSupply extends BaseSupply{
    public BombSupply(double x, double y, double speedX, double speedY) {
        super(x, y, speedX, speedY);
    }

    @Override
    public void takeEffect(WorldHandle world) {
        super.takeEffect(world);
//        world.clearEnemies();
//        world.clearBullets();
//        if(world.getWithMusic()) {
//            MusicThread musicThread = new MusicThread(MusicManager.getMusicPath("bomb_explosion"), false);
//            musicThread.start();
//        }
//        world.clearSupplies();

        listeners.removeIf(BombListener::notValid);
        for (BombListener listener : listeners) {
            listener.onBomb();
        }
    }

    static public List<BombListener> listeners = new LinkedList<>();
    static public void addBombListener(BombListener listener) {
        listeners.add(listener);
    }
//    static public void removeBombListener(BombListener listener) {
//        listeners.remove(listener);
//    }
// 容易导致ConconcurrentModificationException，故弃用
}
