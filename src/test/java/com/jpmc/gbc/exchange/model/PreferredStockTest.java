package com.jpmc.gbc.exchange.model;

import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;

/**
 * Created by Kiran Kolli on 03-09-2016.
 */
public class PreferredStockTest {

    @Test
    public void testEquals_positive() {
        PreferredStock abcPreferredStock = new PreferredStock("ABC", new Price(1D, Currency.USD), 1D, new Price(1D, Currency.USD));
        PreferredStock defPreferredStock = new PreferredStock("DEF", new Price(1D, Currency.USD), 1D, new Price(1D, Currency.USD));

        Assert.assertTrue(!Objects.equals(abcPreferredStock, defPreferredStock));

        PreferredStock abcCloneStock = new PreferredStock("ABC", new Price(1D, Currency.USD), 1D, new Price(1D, Currency.USD));
        Assert.assertTrue(Objects.equals(abcPreferredStock, abcCloneStock));
    }

    @Test
    public void testHashCode_positive() {
        PreferredStock abcPreferredStock = new PreferredStock("ABC", new Price(1D, Currency.USD), 1D, new Price(1D, Currency.USD));
        PreferredStock defPreferredStock = new PreferredStock("DEF", new Price(1D, Currency.USD), 1D, new Price(1D, Currency.USD));

        Assert.assertTrue(abcPreferredStock.hashCode() != defPreferredStock.hashCode());

        PreferredStock abcCloneStock = new PreferredStock("ABC", new Price(1D, Currency.GBP), 1D, new Price(1D, Currency.USD));
        Assert.assertTrue(abcPreferredStock.hashCode() == abcCloneStock.hashCode());
    }

    @Test
    public void testDividendYield_positive() {
        PreferredStock ginStock = new PreferredStock("GIN", new Price(8D, Currency.USD), 2, new Price(100D, Currency.USD));

        Price dividendYield = ginStock.getDividendYield(new Price(4D, Currency.USD));
        Assert.assertEquals(0.5D, dividendYield.getAsBigDecimal().doubleValue(), 0D);
    }
}
