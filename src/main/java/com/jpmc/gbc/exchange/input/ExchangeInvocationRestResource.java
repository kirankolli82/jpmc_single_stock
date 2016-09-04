package com.jpmc.gbc.exchange.input;

import com.jpmc.gbc.exchange.data.DuplicateStockCreationException;
import com.jpmc.gbc.exchange.data.StockCache;
import com.jpmc.gbc.exchange.data.StockNotFoundException;
import com.jpmc.gbc.exchange.index.GBCEAllSharesIndex;
import com.jpmc.gbc.exchange.model.*;
import com.jpmc.gbc.exchange.trade.execution.StockTradeExecutionPlatform;
import com.jpmc.gbc.exchange.trade.execution.UnknownStockException;
import com.jpmc.gbc.exchange.trade.heuristics.HeuristicNotAvailableException;
import com.jpmc.gbc.exchange.trade.heuristics.VolumeWeightedStockPriceCalculator;
import org.apache.commons.lang3.StringUtils;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Created by Kiran Kolli on 04-09-2016.
 */
@Path("gbcexchange")
public class ExchangeInvocationRestResource {

    private final StockCache stockCache;
    private final StockTradeExecutionPlatform stockTradeExecutionPlatform;
    private final VolumeWeightedStockPriceCalculator volumeWeightedStockPriceCalculator;
    private final GBCEAllSharesIndex allSharesIndex;

    public ExchangeInvocationRestResource(StockCache stockCache, StockTradeExecutionPlatform stockTradeExecutionPlatform, VolumeWeightedStockPriceCalculator volumeWeightedStockPriceCalculator, GBCEAllSharesIndex allSharesIndex) {
        this.stockCache = stockCache;
        this.stockTradeExecutionPlatform = stockTradeExecutionPlatform;
        this.volumeWeightedStockPriceCalculator = volumeWeightedStockPriceCalculator;
        this.allSharesIndex = allSharesIndex;
    }


    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("stock/create/common")
    public String createCommonStock(@QueryParam("symbol") String symbol,
                                    @QueryParam("dividend") Double dividend,
                                    @QueryParam("currency") String currencyString) {
        String error = validateSymbol(symbol);

        if (error != null) {
            return error;
        }

        error = validateCurrency(currencyString);

        if (error != null) {
            return error;
        }

        CommonStock commonStock =
                new CommonStock(symbol, new Price(dividend, Currency.valueOf(currencyString)));
        try {
            this.stockCache.createStock(commonStock);
        } catch (DuplicateStockCreationException e) {
            return "Duplicate stock creation attempt detected! Duplicate not created!";
        }

        return "Common stock created : " + commonStock;

    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("stock/create/preferred")
    public String createPreferredStock(@QueryParam("symbol") String symbol,
                                       @QueryParam("dividend") Double dividend,
                                       @QueryParam("currency") String currencyString,
                                       @QueryParam("fixedDividendPercentage") Double fixedDividendPercentage,
                                       @QueryParam("parValue") Double parValue) {
        String error = validateSymbol(symbol);

        if (error != null) {
            return error;
        }

        error = validateCurrency(currencyString);

        if (error != null) {
            return error;
        }

        Currency currency = Currency.valueOf(currencyString);
        PreferredStock preferredStock =
                new PreferredStock(symbol, new Price(dividend, currency),
                        fixedDividendPercentage, new Price(parValue, currency));
        try {
            this.stockCache.createStock(preferredStock);
        } catch (DuplicateStockCreationException e) {
            return "Duplicate stock creation attempt detected! Duplicate not created!";
        }

        return "Preferred stock created : " + preferredStock;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("exchange/execute")
    public String executeTrade(@QueryParam("symbol") String symbol,
                               @QueryParam("stockType") String stockTypeString,
                               @QueryParam("currency") String currencyString,
                               @QueryParam("tradeSettlementPrice") Double tradeSettlementPrice,
                               @QueryParam("direction") String directionString,
                               @QueryParam("quantity") Long quantity) {
        String error = validateSymbol(symbol);

        if (error != null) {
            return error;
        }

        error = validateCurrency(currencyString);

        if (error != null) {
            return error;
        }

        error = validateStockType(stockTypeString);

        if (error != null) {
            return error;
        }

        error = validateDirection(directionString);

        if (error != null) {
            return error;
        }

        StockIdentifier stockIdentifier = new StockIdentifier(symbol, StockType.valueOf(stockTypeString));
        LocalDateTime tradeExecutionTime = LocalDateTime.now();
        Currency currency = Currency.valueOf(currencyString);
        Price tradePrice = new Price(tradeSettlementPrice, currency);
        TradeDirection tradeDirection = TradeDirection.valueOf(directionString);

        TradeDetails tradeDetails = new TradeDetailsBuilder()
                .setStockIdentifier(stockIdentifier)
                .setTradeExecutionTime(tradeExecutionTime)
                .setTradeSettlementPrice(tradePrice)
                .setTradeDirection(tradeDirection)
                .setQuantity(quantity).build();

        try {
            stockTradeExecutionPlatform.executeTrade(tradeDetails);
        } catch (UnknownStockException e) {
            return "Trade being executed for an unknown stock! Initialize the stock first using " +
                    "stock/create/preferred or stock/create/common";
        }

        return "Trade executed";
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("heuristics/getVolumeWeightedStockPrice")
    public String getVolumeWeightedStockPrice(@QueryParam("symbol") String symbol,
                                              @QueryParam("stockType") String stockTypeString) {
        String error = validateSymbol(symbol);

        if (error != null) {
            return error;
        }

        error = validateStockType(stockTypeString);

        if (error != null) {
            return error;
        }

        StockIdentifier stockIdentifier = new StockIdentifier(symbol, StockType.valueOf(stockTypeString));

        try {
            Price price = volumeWeightedStockPriceCalculator.getVolumeWeightedStockPrice(stockIdentifier);
            return "Volume Weight Stock Price:" + price.getAsBigDecimal()
                    + " ,Currency:" + price.getCurrency();
        } catch (HeuristicNotAvailableException e) {
            return "VolumeWeightedStockPrice not available for stock:" + stockIdentifier;
        }
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("index/getShareIndex")
    public String getShareIndex() {
        Map<Currency, BigDecimal> shareIndex = allSharesIndex.getAllShareIndex();
        StringBuilder indexMapBuilder = new StringBuilder();
        for (Currency currency : shareIndex.keySet()) {
            indexMapBuilder.append("Index for: ").append(currency.name())
                    .append(" is ").append(shareIndex.get(currency).toString());
            indexMapBuilder.append(System.lineSeparator());
        }
        return indexMapBuilder.toString();
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("stock/getPERatio")
    public String getPERatio(@QueryParam("symbol") String symbol,
                             @QueryParam("stockType") String stockTypeString,
                             @QueryParam("currency") String currencyString,
                             @QueryParam("tradeSettlementPrice") Double tradeSettlementPrice) {
        String error = validateSymbol(symbol);

        if (error != null) {
            return error;
        }

        error = validateStockType(stockTypeString);

        if (error != null) {
            return error;
        }

        error = validateCurrency(currencyString);

        if (error != null) {
            return error;
        }

        StockIdentifier stockIdentifier = new StockIdentifier(symbol, StockType.valueOf(stockTypeString));

        try {
            Stock stock = stockCache.getStock(stockIdentifier);
            BigDecimal peRatio =
                    stock.getPERatio(new Price(tradeSettlementPrice, Currency.valueOf(currencyString)));
            return "P/E Ratio for " + stockIdentifier + " is " + peRatio.toString();
        } catch (StockNotFoundException e) {
            return "Stock not found!";
        }
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("stock/getDividendYield")
    public String getDividendYield(@QueryParam("symbol") String symbol,
                                   @QueryParam("stockType") String stockTypeString,
                                   @QueryParam("currency") String currencyString,
                                   @QueryParam("tradeSettlementPrice") Double tradeSettlementPrice) {
        String error = validateSymbol(symbol);

        if (error != null) {
            return error;
        }

        error = validateStockType(stockTypeString);

        if (error != null) {
            return error;
        }

        error = validateCurrency(currencyString);

        if (error != null) {
            return error;
        }

        StockIdentifier stockIdentifier = new StockIdentifier(symbol, StockType.valueOf(stockTypeString));

        try {
            Stock stock = stockCache.getStock(stockIdentifier);
            Price dividendYield =
                    stock.getDividendYield(new Price(tradeSettlementPrice, Currency.valueOf(currencyString)));
            return "Dividend yield for " + stockIdentifier + " is " + dividendYield.toString();
        } catch (StockNotFoundException e) {
            return "Stock not found!";
        }
    }

    private String validateSymbol(String symbol) {
        if (StringUtils.isBlank(symbol)) {
            return "Symbol should not be blank!";
        }
        return null;
    }

    private String validateStockType(String stockTypeString) {
        try {
            StockType stockType = StockType.valueOf(stockTypeString);
        } catch (IllegalArgumentException e) {
            return "Stock type should be one of:" + StockType.COMMON.name()
                    + "/" + StockType.PREFERRED.name();
        }
        return null;
    }

    private String validateCurrency(String currencyString) {
        try {
            Currency currency = Currency.valueOf(currencyString);
        } catch (IllegalArgumentException e) {
            return "Currency should be one of:" + Currency.USD.name() + "/" + Currency.GBP.name();
        }
        return null;
    }

    private String validateDirection(String direction) {
        try {
            TradeDirection tradeDirection = TradeDirection.valueOf(direction);
        } catch (IllegalArgumentException e) {
            return "TradeDirection should be one of:" + TradeDirection.BUY.name()
                    + "/" + TradeDirection.SELL.name();
        }
        return null;
    }

}
