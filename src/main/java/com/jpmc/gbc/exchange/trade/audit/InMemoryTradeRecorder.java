package com.jpmc.gbc.exchange.trade.audit;

import com.jpmc.gbc.exchange.model.TradeDetails;
import com.jpmc.gbc.exchange.trade.execution.TradeAware;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Kiran Kolli on 04-09-2016.
 */
class InMemoryTradeRecorder implements TradeRecorder, Closeable {

    private final List<TradeDetails> inMemoryQueue = new ArrayList<>();
    private final Object lock = new Object();
    private final TradeAware tradeAware;
    private final Object subscriptionHandle;

    InMemoryTradeRecorder(TradeAware tradeAware) {
        this.tradeAware = tradeAware;
        subscriptionHandle = this.tradeAware.subscribeToTrades(tradeDetails -> {
            try {
                recordTrade(tradeDetails);
            } catch (RecordingException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void recordTrade(TradeDetails tradeDetails) throws RecordingException {
        synchronized (lock) {
            boolean success = inMemoryQueue.add(tradeDetails);
            if (!success) {
                throw new RecordingException("Recording capcity is full, cannot record any more");
            }
        }
    }

    @Override
    public List<TradeDetails> replayTrades() {
        synchronized (lock) {
            return new ArrayList<>(inMemoryQueue);
        }
    }

    @Override
    public void close() throws IOException {
        synchronized (lock) {
            if (subscriptionHandle != null) {
                tradeAware.unsubscribe(subscriptionHandle);
            }
        }
    }
}
