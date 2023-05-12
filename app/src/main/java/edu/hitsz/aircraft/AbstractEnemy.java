package edu.hitsz.aircraft;

import edu.hitsz.game.EnemyVanishListener;
import edu.hitsz.shootStrategy.BaseShootStrategy;
import edu.hitsz.supply.BaseSupply;
import edu.hitsz.supply.BombListener;
import edu.hitsz.supply.BombSupply;

import java.util.LinkedList;
import java.util.List;

public abstract class AbstractEnemy extends AbstractAircraft implements BombListener{
    public AbstractEnemy(double locationX, double locationY, double speedX, double speedY, int hp, BaseShootStrategy shootStrategy) {
        super(locationX, locationY, speedX, speedY, hp, shootStrategy);
        BombSupply.addBombListener(this);
    }

    protected List<BaseSupply> produceSupply() {
        return new LinkedList<>();
    }

    protected List<EnemyVanishListener> listeners;

    public void addEnemyVanishListener(EnemyVanishListener listener) {
        if (listeners == null) {
            listeners = new LinkedList<>();
        }
        listeners.add(listener);
    }

    public void removeEnemyVanishListener(EnemyVanishListener listener) {
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    @Override
    public void vanish() {
        super.vanish();
        if (listeners != null) {
            for (EnemyVanishListener listener : listeners) {
                listener.onEnemyVanish(this.getScore(), this.isBoss(), this.produceSupply());
            }
        }
    }

    protected int score = 0;
    protected int getScore() {
        return score;
    }

    protected boolean isBoss() {
        return false;
    }

    public void onBomb() {
        vanish();
    }
}