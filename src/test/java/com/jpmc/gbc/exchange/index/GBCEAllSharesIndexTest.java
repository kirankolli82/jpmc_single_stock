package com.jpmc.gbc.exchange.index;

import com.jpmc.gbc.exchange.data.StockCache;
import com.jpmc.gbc.exchange.model.*;
import com.jpmc.gbc.exchange.model.Currency;
import com.jpmc.gbc.exchange.trade.heuristics.VolumeWeightedStockPriceCalculator;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentMatcher;

import java.math.BigDecimal;
import java.util.*;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by Kiran Kolli on 04-09-2016.
 */
public class GBCEAllSharesIndexTest {

    @Test
    public void testCorrectShareIndexCalculation_positive() {
        StockCache stockCache = mock(StockCache.class);
        VolumeWeightedStockPriceCalculator volumeWeightedStockPriceCalculator =
                mock(VolumeWeightedStockPriceCalculator.class);
        GBCEAllSharesIndex underTest = new GBCEAllSharesIndex(stockCache, volumeWeightedStockPriceCalculator);
        StockIdentifier abcStockIdentifier = new StockIdentifier("ABC", StockType.COMMON);
        StockIdentifier defStockIdentifier = new StockIdentifier("DEF", StockType.COMMON);
        when(stockCache.getAllStocks()).thenReturn(Arrays.asList(
                new CommonStock("ABC", new Price(10D, Currency.USD)),
                new CommonStock("DEF", new Price(20D, Currency.USD))));
        Set<StockIdentifier> stockIdentifiers = new HashSet<>();
        stockIdentifiers.add(abcStockIdentifier);
        stockIdentifiers.add(defStockIdentifier);
        Map<StockIdentifier, Price> pricesToReturn = new HashMap<>();
        pricesToReturn.put(abcStockIdentifier, new Price(5D, Currency.USD));
        pricesToReturn.put(defStockIdentifier, new Price(20D, Currency.USD));

        when(volumeWeightedStockPriceCalculator.getVolumeWeightedStockPrices(argThat(new ArgumentMatcher<Set<StockIdentifier>>() {
            @Override
            public boolean matches(Object o) {
                return Objects.equals(o, stockIdentifiers);
            }
        }))).thenReturn(pricesToReturn);

        Map<Currency, BigDecimal> index = underTest.getAllShareIndex();
        Assert.assertEquals(1, index.size());
        Assert.assertEquals(10D, index.get(Currency.USD).doubleValue(), 0D);
    }
}
