package com.jpmc.gbc.exchange.trade.execution;

import com.jpmc.gbc.exchange.model.TradeDetails;

/**
 * Created by Kiran Kolli on 03-09-2016.
 */
public interface StockTradeExecutionPlatform {

    void executeTrade(TradeDetails tradeDetails) throws UnknownStockException;
}
