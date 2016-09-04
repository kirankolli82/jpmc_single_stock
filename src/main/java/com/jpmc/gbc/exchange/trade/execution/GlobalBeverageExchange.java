package com.jpmc.gbc.exchange.trade.execution;

import com.jpmc.gbc.exchange.common.pattern.SubscriptionManager;
import com.jpmc.gbc.exchange.data.StockCache;
import com.jpmc.gbc.exchange.data.StockNotFoundException;
import com.jpmc.gbc.exchange.model.StockIdentifier;
import com.jpmc.gbc.exchange.model.TradeDetails;

/**
 * Created by Kiran Kolli on 03-09-2016.
 */
public class GlobalBeverageExchange implements StockTradeExecutionPlatform, TradeAware {

    private final StockCache stockCache;
    private final SubscriptionManager<TradeSubscriber> subscriptionManager =
            new SubscriptionManager<>();

    public GlobalBeverageExchange(StockCache stockCache) {
        this.stockCache = stockCache;
    }

    @Override
    public void executeTrade(TradeDetails tradeDetails) throws UnknownStockException {
        StockIdentifier stockIdentifier = tradeDetails.getStockIdentifier();
        try {
            stockCache.getStock(stockIdentifier);
        } catch (StockNotFoundException e) {
            throw new UnknownStockException();
        }
        subscriptionManager.invokeOnAllSubscribers(tradeSubscriber -> tradeSubscriber.onTrade(tradeDetails));
    }

    @Override
    public Object subscribeToTrades(TradeSubscriber tradeSubscriber) {
        return subscriptionManager.subscribe(tradeSubscriber);
    }

    @Override
    public void unsubscribe(Object subscriptionHandle) {
        subscriptionManager.unsubscribe(subscriptionHandle);
    }
}
