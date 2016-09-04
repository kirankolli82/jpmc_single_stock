package com.jpmc.gbc.exchange.index;

import com.jpmc.gbc.exchange.data.StockCache;
import com.jpmc.gbc.exchange.model.Currency;
import com.jpmc.gbc.exchange.model.Price;
import com.jpmc.gbc.exchange.model.Stock;
import com.jpmc.gbc.exchange.model.StockIdentifier;
import com.jpmc.gbc.exchange.trade.heuristics.VolumeWeightedStockPriceCalculator;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by Kiran Kolli on 04-09-2016.
 */
public class GBCEAllSharesIndex {

    private static final int DEFAULT_PRECISION = 8;
    private final StockCache stockCache;
    private final VolumeWeightedStockPriceCalculator volumeWeightedStockPriceCalculator;

    public GBCEAllSharesIndex(StockCache stockCache, VolumeWeightedStockPriceCalculator volumeWeightedStockPriceCalculator) {
        this.stockCache = stockCache;
        this.volumeWeightedStockPriceCalculator = volumeWeightedStockPriceCalculator;
    }

    public Map<Currency, BigDecimal> getAllShareIndex() {
        List<Stock> stocks = stockCache.getAllStocks();
        Set<StockIdentifier> stockIdentifiers = stocks.stream().map(stock -> stock.getStockIdentifier())
                .collect(Collectors.toSet());

        Map<StockIdentifier, Price> prices =
                volumeWeightedStockPriceCalculator.getVolumeWeightedStockPrices(stockIdentifiers);

        int size = prices.size();

        if (size == 0) {
            return new HashMap<>();
        }

        Map<Currency, List<Price>> groupedByCurrency =
                prices.values().stream().collect(Collectors.groupingBy(Price::getCurrency));

        Map<Currency, BigDecimal> shareIndexMap = new HashMap<>();

        for (Currency currency : groupedByCurrency.keySet()) {
            List<Price> priceList = groupedByCurrency.get(currency);
            Price multipliedPrice = priceList.stream().reduce(Price::multiply).get();
            BigDecimal index = new BigDecimal(Math.pow(multipliedPrice.getAsBigDecimal().doubleValue(), 1D / size),
                    new MathContext(DEFAULT_PRECISION));
            shareIndexMap.put(currency, index);
        }

        return shareIndexMap;

    }
}
