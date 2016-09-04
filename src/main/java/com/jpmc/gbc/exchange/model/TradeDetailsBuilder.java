package com.jpmc.gbc.exchange.model;

import java.time.LocalDateTime;

public class TradeDetailsBuilder {
    private StockIdentifier stockIdentifier;
    private LocalDateTime tradeExecutionTime;
    private Price tradeSettlementPrice;
    private long quantity;
    private TradeDirection tradeDirection;

    public TradeDetailsBuilder setStockIdentifier(StockIdentifier stockIdentifier) {
        this.stockIdentifier = stockIdentifier;
        return this;
    }

    public TradeDetailsBuilder setTradeExecutionTime(LocalDateTime tradeExecutionTime) {
        this.tradeExecutionTime = tradeExecutionTime;
        return this;
    }

    public TradeDetailsBuilder setTradeSettlementPrice(Price tradeSettlementPrice) {
        this.tradeSettlementPrice = tradeSettlementPrice;
        return this;
    }

    public TradeDetailsBuilder setQuantity(long quantity) {
        this.quantity = quantity;
        return this;
    }

    public TradeDetailsBuilder setTradeDirection(TradeDirection tradeDirection) {
        this.tradeDirection = tradeDirection;
        return this;
    }

    public TradeDetails build() {
        return new TradeDetails(stockIdentifier, tradeExecutionTime, tradeSettlementPrice, quantity, tradeDirection);
    }
}