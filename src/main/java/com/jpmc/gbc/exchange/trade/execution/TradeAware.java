package com.jpmc.gbc.exchange.trade.execution;

/**
 * Created by Kiran Kolli on 03-09-2016.
 */
public interface TradeAware {

    Object subscribeToTrades(TradeSubscriber tradeSubscriber);

    void unsubscribe(Object subscriptionHandle);
}
