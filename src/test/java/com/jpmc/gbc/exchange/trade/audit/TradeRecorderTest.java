package com.jpmc.gbc.exchange.trade.audit;

import com.jpmc.gbc.exchange.model.*;
import com.jpmc.gbc.exchange.trade.execution.TradeAware;
import com.jpmc.gbc.exchange.trade.execution.TradeSubscriber;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by Kiran Kolli on 04-09-2016.
 */
public class TradeRecorderTest {

    @Test
    public void testTradeRecording_positive() throws RecordingException {
        TradeAware tradeAware = mock(TradeAware.class);
        TradeRecorder tradeRecorder = new InMemoryTradeRecorder(tradeAware);
        ArgumentCaptor<TradeSubscriber> tradeSubscriberArgumentCaptor = ArgumentCaptor.forClass(TradeSubscriber.class);
        verify(tradeAware).subscribeToTrades(tradeSubscriberArgumentCaptor.capture());
        TradeSubscriber subscriber = tradeSubscriberArgumentCaptor.getValue();

        StockIdentifier stockIdentifier = new StockIdentifier("ABC", StockType.COMMON);
        LocalDateTime tradeExecutionTime = LocalDateTime.now();
        Price tradeSettlementPrice = new Price(17D, Currency.USD);

        TradeDetails tradeDetails = new TradeDetailsBuilder()
                .setStockIdentifier(stockIdentifier)
                .setTradeExecutionTime(tradeExecutionTime)
                .setTradeSettlementPrice(tradeSettlementPrice)
                .setTradeDirection(TradeDirection.BUY)
                .setQuantity(100L).build();

        subscriber.onTrade(tradeDetails);

        List<TradeDetails> replayedTrades = tradeRecorder.replayTrades();
        Assert.assertEquals(1, replayedTrades.size());
        TradeDetails replayed = replayedTrades.get(0);
        Assert.assertEquals(stockIdentifier, replayed.getStockIdentifier());
        Assert.assertEquals(tradeExecutionTime, replayed.getTradeExecutionTime());
        Assert.assertEquals(tradeSettlementPrice, replayed.getTradeSettlementPrice());
        Assert.assertEquals(TradeDirection.BUY, replayed.getTradeDirection());
        Assert.assertEquals(100L, replayed.getQuantity());
    }
}
