package com.jpmc.gbc.exchange.data;

import com.jpmc.gbc.exchange.model.Stock;
import com.jpmc.gbc.exchange.model.StockIdentifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Kiran Kolli on 03-09-2016.
 */
public class InMemoryBasedStockCache implements StockCache {

    private final Map<StockIdentifier, Stock> cache = new ConcurrentHashMap<>();

    @Override
    public void createStock(Stock stock) throws DuplicateStockCreationException, IllegalArgumentException {
        if (stock == null) {
            throw new IllegalArgumentException("Stock being created cannot be null");
        }
        Stock inCache = cache.putIfAbsent(stock.getStockIdentifier(), stock);
        if (inCache != null) {
            throw new DuplicateStockCreationException();
        }
    }

    @Override
    public Stock deleteStock(StockIdentifier stockIdentifier) {
        return cache.remove(stockIdentifier);
    }

    @Override
    public Stock getStock(StockIdentifier stockIdentifier) throws StockNotFoundException {
        Stock toReturn = cache.get(stockIdentifier);
        if (toReturn == null) {
            throw new StockNotFoundException();
        }

        return toReturn;
    }

    @Override
    public List<Stock> getAllStocks() {
        return new ArrayList<>(cache.values());
    }
}
