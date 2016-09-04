package com.jpmc.gbc.exchange.model;

import java.math.BigDecimal;

/**
 * Created by Kiran Kolli on 03-09-2016.
 */
public abstract class Stock {

    protected final StockIdentifier stockIdentifier;
    @SuppressWarnings("WeakerAccess")
    protected final Price lastDividend;

    protected Stock(StockIdentifier stockIdentifier, Price lastDividend) {
        this.stockIdentifier = stockIdentifier;
        this.lastDividend = lastDividend;
    }

    public abstract Price getDividendYield(Price price);

    public BigDecimal getPERatio(Price price) {
        return price.divide(lastDividend).getAsBigDecimal();
    }

    public StockIdentifier getStockIdentifier() {
        return stockIdentifier;
    }

    public Price getLastDividend() {
        return lastDividend;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Stock stock = (Stock) o;

        return stockIdentifier != null ? stockIdentifier.equals(stock.stockIdentifier) : stock.stockIdentifier == null;

    }

    @Override
    public int hashCode() {
        return stockIdentifier != null ? stockIdentifier.hashCode() : 0;
    }

    @Override
    public String
    toString() {
        return "Stock{" +
                "stockIdentifier=" + stockIdentifier +
                ", lastDividend=" + lastDividend +
                '}';
    }
}
