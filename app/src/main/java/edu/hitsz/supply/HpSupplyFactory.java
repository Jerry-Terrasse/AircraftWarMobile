package edu.hitsz.supply;

public class HpSupplyFactory implements SupplyFactory{
    private static final HpSupplyFactory INSTANCE = new HpSupplyFactory();
    private HpSupplyFactory() {}
    public static HpSupplyFactory getInstance() {
        return INSTANCE;
    }
    @Override
    public BaseSupply createSupply(double x, double y, double vx, double vy) {
        return new HpSupply(x, y, vx, vy, 20);
    }
}
