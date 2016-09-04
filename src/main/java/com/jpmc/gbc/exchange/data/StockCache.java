package com.jpmc.gbc.exchange.data;

import com.jpmc.gbc.exchange.model.Stock;
import com.jpmc.gbc.exchange.model.StockIdentifier;

import java.util.List;

/**
 * Created by Kiran Kolli on 03-09-2016.
 */
public interface StockCache {

    void createStock(Stock stock) throws DuplicateStockCreationException, IllegalArgumentException;

    Stock deleteStock(StockIdentifier stockIdentifier);

    Stock getStock(StockIdentifier stockIdentifier) throws StockNotFoundException;

    List<Stock> getAllStocks();
}
