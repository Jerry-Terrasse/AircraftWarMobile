package edu.hitsz.aircraft;

public class MobEnemyFactory implements EnemyFactory {
    final static MobEnemyFactory INSTANCE = new MobEnemyFactory();
    private MobEnemyFactory() {}
    public static MobEnemyFactory getInstance() {
        return INSTANCE;
    }
    @Override
    public AbstractEnemy createAircraft(double x, double y, double vx, double vy) {
        return new MobEnemy(x, y, vx, vy, 30);
    }
}