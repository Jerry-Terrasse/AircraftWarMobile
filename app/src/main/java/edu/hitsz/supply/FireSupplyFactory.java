package edu.hitsz.supply;

public class FireSupplyFactory implements SupplyFactory{
    private static final FireSupplyFactory INSTANCE = new FireSupplyFactory();
    private FireSupplyFactory() {}
    public static FireSupplyFactory getInstance() {
        return INSTANCE;
    }
    @Override
    public BaseSupply createSupply(double x, double y, double vx, double vy) {
        return new FireSupply(x, y, vx, vy);
    }
}
