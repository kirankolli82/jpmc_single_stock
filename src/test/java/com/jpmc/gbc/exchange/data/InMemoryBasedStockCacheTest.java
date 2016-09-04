package com.jpmc.gbc.exchange.data;

import com.jpmc.gbc.exchange.model.CommonStock;
import com.jpmc.gbc.exchange.model.Currency;
import com.jpmc.gbc.exchange.model.Price;
import com.jpmc.gbc.exchange.model.Stock;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by Kiran Kolli on 03-09-2016.
 */
public class InMemoryBasedStockCacheTest {

    @Test
    public void testCreation_positive() throws DuplicateStockCreationException, StockNotFoundException {
        InMemoryBasedStockCache underTest = new InMemoryBasedStockCache();

        CommonStock toCreate = new CommonStock("ABC", new Price(100D, Currency.USD));

        underTest.createStock(toCreate);

        Stock retrieved = underTest.getStock(toCreate.getStockIdentifier());

        Assert.assertEquals(toCreate, retrieved);
    }

    @Test
    public void testDeletion_positive() throws StockNotFoundException, DuplicateStockCreationException {
        InMemoryBasedStockCache underTest = new InMemoryBasedStockCache();

        CommonStock toCreate = new CommonStock("ABC", new Price(100D, Currency.USD));

        underTest.createStock(toCreate);

        Stock retrieved = underTest.getStock(toCreate.getStockIdentifier());

        Assert.assertEquals(toCreate, retrieved);

        underTest.deleteStock(toCreate.getStockIdentifier());
        boolean notFound = false;
        try {
            underTest.getStock(toCreate.getStockIdentifier());
        } catch (StockNotFoundException e) {
            notFound = true;
        }

        Assert.assertTrue(notFound);


    }

    @Test(expected = DuplicateStockCreationException.class)
    public void duplicateCreation_positive() throws DuplicateStockCreationException {
        InMemoryBasedStockCache underTest = new InMemoryBasedStockCache();

        CommonStock toCreate = new CommonStock("ABC", new Price(100D, Currency.USD));

        underTest.createStock(toCreate);

        underTest.createStock(toCreate);
    }


}
