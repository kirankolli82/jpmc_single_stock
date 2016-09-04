package com.jpmc.gbc.exchange.model;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Kiran Kolli on 03-09-2016.
 */
public class PriceTest {

    @Test
    public void testPrecision_positive() {
        double operand1 = 1D;
        double operand2 = 3D;

        Price price = new Price(operand1 / operand2, Currency.USD);

        Assert.assertTrue(price.getAsBigDecimal().scale() == Price.getDefaultPrecision());

        StringBuilder priceStringValue = new StringBuilder("0.");
        for (int index = 0; index < Price.getDefaultPrecision(); index++) {
            priceStringValue.append("3");
        }

        Assert.assertEquals(priceStringValue.toString(), price.getAsBigDecimal().toString());
    }

    @Test
    public void testPriceEquals_positive() {
        Price price1 = new Price(7D, Currency.USD);
        Price price2 = new Price(13D, Currency.USD);

        Assert.assertNotEquals(price1, price2);

        price2 = new Price(7D, Currency.GBP);

        Assert.assertNotEquals(price1, price2);

        price2 = new Price(7D, Currency.USD);

        Assert.assertEquals(price1, price2);
    }
}
