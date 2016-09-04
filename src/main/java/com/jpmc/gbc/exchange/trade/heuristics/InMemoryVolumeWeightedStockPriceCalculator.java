package com.jpmc.gbc.exchange.trade.heuristics;

import com.jpmc.gbc.exchange.model.Currency;
import com.jpmc.gbc.exchange.model.Price;
import com.jpmc.gbc.exchange.model.StockIdentifier;
import com.jpmc.gbc.exchange.model.TradeDetails;
import com.jpmc.gbc.exchange.trade.execution.TradeAware;

import java.io.Closeable;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Created by Kiran Kolli on 04-09-2016.
 */
public class InMemoryVolumeWeightedStockPriceCalculator implements VolumeWeightedStockPriceCalculator, Closeable {

    private static final int TRADE_PERSISTENCE_DURATION_IN_SECONDS = 300;
    private final Map<StockIdentifier, TreeMap<LocalDateTime, TradeDetails>> tradeCache = new ConcurrentHashMap<>();
    private final TradeAware tradeAware;
    private final AtomicReference<Object> subscriptionHandle = new AtomicReference<>();

    public InMemoryVolumeWeightedStockPriceCalculator(TradeAware tradeAware) {
        this.tradeAware = tradeAware;
        Object handle = this.tradeAware.subscribeToTrades(tradeDetails -> addToTradeCache(tradeDetails));
        subscriptionHandle.set(handle);
    }

    @Override
    public Price getVolumeWeightedStockPrice(StockIdentifier stockIdentifier) throws HeuristicNotAvailableException {
        LocalDateTime maxAllowedAge = LocalDateTime.now().minusSeconds(getTradePersistenceDurationInSeconds());
        TreeMap<LocalDateTime, TradeDetails> trades = getTradesSnapshot(stockIdentifier);
        trades = new TreeMap<>(trades.tailMap(maxAllowedAge));

        if (trades.isEmpty()) {
            throw new HeuristicNotAvailableException();
        }

        return getVolumeWeightedPriceFor(trades.values());
    }

    @Override
    public Map<StockIdentifier, Price> getVolumeWeightedStockPrices(Set<StockIdentifier> stockIdentifiers) {
        Map<StockIdentifier, TreeMap<LocalDateTime, TradeDetails>> allTrades = getTradesSnapshot();

        Map<StockIdentifier, Price> toReturn = new HashMap<>();
        stockIdentifiers.forEach(stockIdentifier -> {
            Map<LocalDateTime, TradeDetails> trades = allTrades.get(stockIdentifier);
            if (trades != null) {
                toReturn.put(stockIdentifier, getVolumeWeightedPriceFor(trades.values()));
            }
        });
        return toReturn;
    }

    @Override
    public void close() throws IOException {
        if (subscriptionHandle.get() != null) {
            tradeAware.unsubscribe(subscriptionHandle.get());
            subscriptionHandle.set(null);
        }
    }

    private Price getVolumeWeightedPriceFor(Collection<TradeDetails> trades) {
        double totalValue = 0D;
        long totalQuantity = 0L;
        Currency currency = null;
        for (TradeDetails tradeDetails : trades) {
            currency = tradeDetails.getTradeSettlementPrice().getCurrency();
            totalValue = totalValue + tradeDetails.getTotalTradeValue().getAsBigDecimal().doubleValue();
            totalQuantity = totalQuantity + tradeDetails.getQuantity();
        }

        return new Price(totalValue / totalQuantity, currency);
    }

    private void addToTradeCache(TradeDetails tradeDetails) {
        tradeCache.putIfAbsent(tradeDetails.getStockIdentifier(), new TreeMap<>());
        tradeCache.computeIfPresent(tradeDetails.getStockIdentifier(), (stockIdentifier, localDateTimeTradeDetailsTreeMap) -> {
            addCurrentTradeToMap(tradeDetails, localDateTimeTradeDetailsTreeMap);
            return getMaxAllowedAgeMap(localDateTimeTradeDetailsTreeMap);
        });
    }

    private TreeMap<LocalDateTime, TradeDetails> getTradesSnapshot(StockIdentifier stockIdentifier) {
        return new TreeMap<>(tradeCache.getOrDefault(stockIdentifier, new TreeMap<>()));
    }

    private Map<StockIdentifier, TreeMap<LocalDateTime, TradeDetails>> getTradesSnapshot() {
        return new HashMap<>(tradeCache);
    }

    private TreeMap<LocalDateTime, TradeDetails> getMaxAllowedAgeMap(TreeMap<LocalDateTime, TradeDetails> localDateTimeTradeDetailsTreeMap) {
        LocalDateTime maxAgeAllowed = LocalDateTime.now().minusSeconds(getTradePersistenceDurationInSeconds());
        return new TreeMap<>(localDateTimeTradeDetailsTreeMap.tailMap(maxAgeAllowed));
    }

    int getTradePersistenceDurationInSeconds() {
        return TRADE_PERSISTENCE_DURATION_IN_SECONDS;
    }

    private void addCurrentTradeToMap(TradeDetails tradeDetails, TreeMap<LocalDateTime, TradeDetails> localDateTimeTradeDetailsTreeMap) {
        LocalDateTime localDateTime = tradeDetails.getTradeExecutionTime();
        localDateTimeTradeDetailsTreeMap.put(localDateTime, tradeDetails);
    }
}
