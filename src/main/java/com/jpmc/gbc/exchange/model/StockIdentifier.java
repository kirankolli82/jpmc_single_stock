package com.jpmc.gbc.exchange.model;

/**
 * Created by Kiran Kolli on 03-09-2016.
 */
public class StockIdentifier {

    private final String stockSymbol;
    private final StockType stockType;

    public StockIdentifier(String stockSymbol, StockType stockType) {
        this.stockSymbol = stockSymbol;
        this.stockType = stockType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StockIdentifier that = (StockIdentifier) o;

        //noinspection SimplifiableIfStatement
        if (stockSymbol != null ? !stockSymbol.equals(that.stockSymbol) : that.stockSymbol != null) return false;
        return stockType == that.stockType;

    }

    @Override
    public int hashCode() {
        int result = stockSymbol != null ? stockSymbol.hashCode() : 0;
        result = 31 * result + (stockType != null ? stockType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StockIdentifier{" +
                "stockSymbol='" + stockSymbol + '\'' +
                ", stockType=" + stockType +
                '}';
    }
}
