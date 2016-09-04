package com.jpmc.gbc.exchange.model;


import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;

/**
 * Created by Kiran Kolli on 03-09-2016.
 */
public class CommonStockTest {

    @Test
    public void testEquals_positive() {
        CommonStock abcCommonStock = new CommonStock("ABC", new Price(1D, Currency.USD));
        CommonStock defCommonStock = new CommonStock("DEF", new Price(1D, Currency.USD));

        Assert.assertTrue(!Objects.equals(abcCommonStock, defCommonStock));

        CommonStock abcCloneStock = new CommonStock("ABC", new Price(13D, Currency.GBP));
        Assert.assertTrue(Objects.equals(abcCommonStock, abcCloneStock));
    }

    @Test
    public void testHashCode_positive() {
        CommonStock abcCommonStock = new CommonStock("ABC", new Price(1D, Currency.USD));
        CommonStock defCommonStock = new CommonStock("DEF", new Price(1D, Currency.USD));

        Assert.assertTrue(abcCommonStock.hashCode() != defCommonStock.hashCode());

        CommonStock abcCloneStock = new CommonStock("ABC", new Price(1D, Currency.USD));
        Assert.assertTrue(abcCommonStock.hashCode() == abcCloneStock.hashCode());
    }

    @Test
    public void testDividendYield_positive() {
        CommonStock teaStock = new CommonStock("TEA", new Price(0D, Currency.USD));

        Price dividendYield = teaStock.getDividendYield(new Price(23D, Currency.USD));
        Assert.assertEquals(0D, dividendYield.getAsBigDecimal().doubleValue(), 0D);

        CommonStock popStock = new CommonStock("POP", new Price(8D, Currency.USD));

        dividendYield = popStock.getDividendYield(new Price(16D, Currency.USD));
        Assert.assertEquals(0.5D, dividendYield.getAsBigDecimal().doubleValue(), 0D);
    }
}
