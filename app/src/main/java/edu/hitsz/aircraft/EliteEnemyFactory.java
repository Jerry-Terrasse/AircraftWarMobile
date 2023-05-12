package edu.hitsz.aircraft;

public class EliteEnemyFactory implements EnemyFactory {
    final static EliteEnemyFactory INSTANCE = new EliteEnemyFactory();
    private EliteEnemyFactory() {}
    public static EliteEnemyFactory getInstance() {
        return INSTANCE;
    }
    @Override
    public AbstractEnemy createAircraft(double x, double y, double vx, double vy) {
        return new EliteEnemy(x, y, vx, vy, 50);
    }
}