package com.jpmc.gbc.exchange.trade.audit;

import com.jpmc.gbc.exchange.model.TradeDetails;

import java.util.List;

/**
 * Created by Kiran Kolli on 04-09-2016.
 */
interface TradeRecorder {

    void recordTrade(TradeDetails tradeDetails) throws RecordingException;

    List<TradeDetails> replayTrades();
}
