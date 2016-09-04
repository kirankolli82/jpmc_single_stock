package com.jpmc.gbc.exchange.model;

/**
 * Created by Kiran Kolli on 03-09-2016.
 */
public class PreferredStock extends Stock {
    private final Price dividendParValue;

    public PreferredStock(String symbol, Price lastDividend, double fixedDividendPercentage, Price parValue) {
        super(new StockIdentifier(symbol, StockType.PREFERRED), lastDividend);
        this.dividendParValue =
                new Price((fixedDividendPercentage * parValue.getAsBigDecimal().doubleValue()) / 100D,
                        parValue.getCurrency());
    }

    @Override
    public Price getDividendYield(Price price) {
        return dividendParValue.divide(price);
    }

    @Override
    public String toString() {
        return "PreferredStock{" +
                "dividendParValue=" + dividendParValue +
                "} " + super.toString();
    }
}
