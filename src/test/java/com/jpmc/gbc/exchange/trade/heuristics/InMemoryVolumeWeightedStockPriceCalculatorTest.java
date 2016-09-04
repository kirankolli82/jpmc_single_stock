package com.jpmc.gbc.exchange.trade.heuristics;

import com.jpmc.gbc.exchange.model.*;
import com.jpmc.gbc.exchange.trade.execution.TradeAware;
import com.jpmc.gbc.exchange.trade.execution.TradeSubscriber;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;

import java.io.IOException;
import java.time.LocalDateTime;

import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Created by Kiran Kolli on 04-09-2016.
 */
public class InMemoryVolumeWeightedStockPriceCalculatorTest {

    @Test
    public void testSubscription_positive() {
        TradeAware tradeAware = mock(TradeAware.class);
        InMemoryVolumeWeightedStockPriceCalculator underTest =
                new InMemoryVolumeWeightedStockPriceCalculator(tradeAware);

        verify(tradeAware).subscribeToTrades(argThat(new ArgumentMatcher<TradeSubscriber>() {
            @Override
            public boolean matches(Object o) {
                return o instanceof TradeSubscriber;
            }
        }));
    }

    @Test
    public void testUnSubscription_positive() throws IOException {
        TradeAware tradeAware = mock(TradeAware.class);
        Object handle = new Object();
        doAnswer(invocationOnMock -> handle)
                .when(tradeAware).subscribeToTrades(argThat(new ArgumentMatcher<TradeSubscriber>() {
            @Override
            public boolean matches(Object o) {
                return o instanceof TradeSubscriber;
            }
        }));

        try (InMemoryVolumeWeightedStockPriceCalculator underTest =
                     new InMemoryVolumeWeightedStockPriceCalculator(tradeAware)) {
            verify(tradeAware).subscribeToTrades(argThat(new ArgumentMatcher<TradeSubscriber>() {
                @Override
                public boolean matches(Object o) {
                    return o instanceof TradeSubscriber;
                }
            }));
        }

        verify(tradeAware).unsubscribe(eq(handle));
    }

    @Test
    public void testCorrectVolumeWeightedStockPrice_positive() throws IOException, InterruptedException, HeuristicNotAvailableException {
        TradeAware tradeAware = mock(TradeAware.class);
        ArgumentCaptor<TradeSubscriber> subscriberArgumentCaptor = ArgumentCaptor.forClass(TradeSubscriber.class);
        try (InMemoryVolumeWeightedStockPriceCalculator underTest =
                     new InMemoryVolumeWeightedStockPriceCalculator(tradeAware) {
                         @Override
                         int getTradePersistenceDurationInSeconds() {
                             return 1;
                         }
                     }) {
            verify(tradeAware).subscribeToTrades(subscriberArgumentCaptor.capture());
            TradeSubscriber tradeSubscriber = subscriberArgumentCaptor.getValue();

            TradeDetails tradeDetails = buildTradeDetails("ABC", StockType.COMMON,
                    17D, Currency.USD, TradeDirection.BUY, 100L);
            tradeSubscriber.onTrade(tradeDetails);

            Thread.sleep(500);

            tradeDetails = buildTradeDetails("ABC", StockType.COMMON,
                    20D, Currency.USD, TradeDirection.BUY, 200L);

            tradeSubscriber.onTrade(tradeDetails);

            Price volumeWeightedPrice = underTest.getVolumeWeightedStockPrice(tradeDetails.getStockIdentifier());

            Assert.assertEquals(19D, volumeWeightedPrice.getAsBigDecimal().doubleValue(), 0D);

            Thread.sleep(600);

            volumeWeightedPrice = underTest.getVolumeWeightedStockPrice(tradeDetails.getStockIdentifier());

            Assert.assertEquals(20D, volumeWeightedPrice.getAsBigDecimal().doubleValue(), 0D);
        }
    }

    private TradeDetails buildTradeDetails(String symbol, StockType stockType,
                                           double tradeSettlementPrice, Currency currency,
                                           TradeDirection tradeDirection, long quantity) {
        StockIdentifier stockIdentifier = new StockIdentifier(symbol, stockType);
        LocalDateTime tradeExecutionTime = LocalDateTime.now();
        Price tradePrice = new Price(tradeSettlementPrice, currency);

        return new TradeDetailsBuilder()
                .setStockIdentifier(stockIdentifier)
                .setTradeExecutionTime(tradeExecutionTime)
                .setTradeSettlementPrice(tradePrice)
                .setTradeDirection(tradeDirection)
                .setQuantity(quantity).build();
    }
}
