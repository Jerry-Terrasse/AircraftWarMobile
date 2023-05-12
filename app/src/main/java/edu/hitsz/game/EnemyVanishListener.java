package edu.hitsz.game;

import edu.hitsz.supply.BaseSupply;

import java.util.List;

public interface EnemyVanishListener {
    void onEnemyVanish(int increaseScore, boolean isBoss, List<BaseSupply> dropSupplies);
}
