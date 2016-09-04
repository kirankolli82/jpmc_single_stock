package com.jpmc.gbc.exchange.trade.heuristics;

import com.jpmc.gbc.exchange.model.Price;
import com.jpmc.gbc.exchange.model.StockIdentifier;

import java.util.Map;
import java.util.Set;

/**
 * Created by Kiran Kolli on 03-09-2016.
 */
public interface VolumeWeightedStockPriceCalculator {

    Price getVolumeWeightedStockPrice(StockIdentifier stockIdentifier) throws HeuristicNotAvailableException;

    Map<StockIdentifier, Price> getVolumeWeightedStockPrices(Set<StockIdentifier> stockIdentifiers);
}
