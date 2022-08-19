package uk.tw.energy.domain;

import java.math.BigDecimal;

public class UsageCost {
    private final BigDecimal cost;

    public UsageCost(BigDecimal consumptionCost) {
        this.cost = consumptionCost;
    }

    public BigDecimal getCost() {
        return cost;
    }
}
