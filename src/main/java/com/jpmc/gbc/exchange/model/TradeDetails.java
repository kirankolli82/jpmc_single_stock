package com.jpmc.gbc.exchange.model;

import java.time.LocalDateTime;

/**
 * Created by Kiran Kolli on 03-09-2016.
 */
public class TradeDetails {

    private final StockIdentifier stockIdentifier;
    private final LocalDateTime tradeExecutionTime;
    private final Price tradeSettlementPrice;
    private final long quantity;
    private final TradeDirection tradeDirection;

    TradeDetails(StockIdentifier stockIdentifier, LocalDateTime tradeExecutionTime, Price tradeSettlementPrice, long quantity, TradeDirection tradeDirection) {
        this.stockIdentifier = stockIdentifier;
        this.tradeExecutionTime = tradeExecutionTime;
        this.tradeSettlementPrice = tradeSettlementPrice;
        this.quantity = quantity;
        this.tradeDirection = tradeDirection;
    }

    public StockIdentifier getStockIdentifier() {
        return stockIdentifier;
    }

    public LocalDateTime getTradeExecutionTime() {
        return tradeExecutionTime;
    }

    public Price getTradeSettlementPrice() {
        return tradeSettlementPrice;
    }

    public long getQuantity() {
        return quantity;
    }

    public TradeDirection getTradeDirection() {
        return tradeDirection;
    }

    public Price getTotalTradeValue() {
        return new Price(tradeSettlementPrice.getAsBigDecimal().doubleValue() * quantity,
                tradeSettlementPrice.getCurrency());
    }
}
