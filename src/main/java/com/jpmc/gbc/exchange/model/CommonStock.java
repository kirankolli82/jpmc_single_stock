package com.jpmc.gbc.exchange.model;

/**
 * Created by Kiran Kolli on 03-09-2016.
 */
public class CommonStock extends Stock {

    public CommonStock(String stockSymbol, Price lastDividend) {
        super(new StockIdentifier(stockSymbol, StockType.COMMON), lastDividend);
    }

    @Override
    public Price getDividendYield(Price price) {
        return lastDividend.divide(price);
    }

    @Override
    public String toString() {
        return "CommonStock{} " + super.toString();
    }
}
