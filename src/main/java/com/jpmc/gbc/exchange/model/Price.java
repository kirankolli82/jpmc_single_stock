package com.jpmc.gbc.exchange.model;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Price used in stock exchange precise until 6 decimal places.
 * For the sake of the current treatise we assume all
 * transactions within the exchange take place in a single currency.
 * Created by Kiran Kolli on 03-09-2016.
 */
public class Price {

    private static final int DEFAULT_PRECISION = 8;
    private final BigDecimal price;
    private final Currency currency;

    public Price(double price, Currency currency) {
        this.price = new BigDecimal(price, new MathContext(DEFAULT_PRECISION));
        this.currency = currency;
    }

    public BigDecimal getAsBigDecimal() {
        return price;
    }

    public Currency getCurrency() {
        return currency;
    }

    Price divide(Price price) {
        if (price == null) {
            throw new IllegalArgumentException("Dividing Price cannot be null");
        }

        if (this.currency != price.currency) {
            throw new IllegalArgumentException("Dividing Price must be of same currency");
        }

        return new Price(this.getAsBigDecimal().
                divide(price.getAsBigDecimal(), DEFAULT_PRECISION, BigDecimal.ROUND_DOWN).doubleValue(), currency);
    }

    public Price multiply(Price price) {
        if (price == null) {
            throw new IllegalArgumentException("Multiplying Price cannot be null");
        }

        if (this.currency != price.currency) {
            throw new IllegalArgumentException("Multiplying Price must be of same currency");
        }

        return new Price(this.getAsBigDecimal().multiply(price.getAsBigDecimal()).doubleValue(),
                this.getCurrency());
    }

    /*
    Visibility for tests
     */
    static int getDefaultPrecision() {
        return DEFAULT_PRECISION;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Price price1 = (Price) o;

        //noinspection SimplifiableIfStatement,ConstantConditions
        if (price != null ? !price.equals(price1.price) : price1.price != null) return false;
        return currency == price1.currency;

    }

    @Override
    public int hashCode() {
        int result = price != null ? price.hashCode() : 0;
        result = 31 * result + (currency != null ? currency.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Price{" +
                "price=" + price +
                ", currency=" + currency +
                '}';
    }
}
