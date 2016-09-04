package com.jpmc.gbc.exchange.model;

import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Created by Kiran Kolli on 03-09-2016.
 */
public class StockTest {

    @Test
    public void testChildrenEquals_positive() {
        Stock commonStock = new CommonStock("ABC", new Price(1D, Currency.USD));
        Stock prefferedStock = new PreferredStock("ABC", new Price(1D, Currency.USD), 1D, new Price(1D, Currency.USD));

        Assert.assertTrue(!Objects.equals(commonStock, prefferedStock));
    }

    @Test
    public void testChildrenHashCode_positive() {
        Stock commonStock = new CommonStock("ABC", new Price(1D, Currency.USD));
        Stock prefferedStock = new PreferredStock("ABC", new Price(1D, Currency.USD), 1D, new Price(1D, Currency.USD));

        Assert.assertTrue(commonStock.hashCode() != prefferedStock.hashCode());
    }

    @Test
    public void testGetAsPERatio_positive() {
        CommonStock commonStock = new CommonStock("ABC", new Price(13D, Currency.USD));

        BigDecimal peRatio = commonStock.getPERatio(new Price(26D, Currency.USD));

        Assert.assertEquals(2D, peRatio.doubleValue(), 0D);

        PreferredStock preferredStock =
                new PreferredStock("DEF", new Price(13D, Currency.USD),
                        10, new Price(100D, Currency.USD));

        peRatio = preferredStock.getPERatio(new Price(26D, Currency.USD));

        Assert.assertEquals(2D, peRatio.doubleValue(), 0D);
    }
}
