package com.jpmc.gbc.exchange;

import com.jpmc.gbc.exchange.data.InMemoryBasedStockCache;
import com.jpmc.gbc.exchange.data.StockCache;
import com.jpmc.gbc.exchange.index.GBCEAllSharesIndex;
import com.jpmc.gbc.exchange.input.ExchangeInvocationRestResource;
import com.jpmc.gbc.exchange.trade.execution.GlobalBeverageExchange;
import com.jpmc.gbc.exchange.trade.heuristics.InMemoryVolumeWeightedStockPriceCalculator;
import com.jpmc.gbc.exchange.trade.heuristics.VolumeWeightedStockPriceCalculator;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Created by Kiran Kolli on 04-09-2016.
 */
public class ExchangeStartUp {

    private static final String BASE_URI = "http://localhost:8080/";

    private static HttpServer startServer() {
        StockCache stockCache = new InMemoryBasedStockCache();
        GlobalBeverageExchange globalBeverageExchange = new GlobalBeverageExchange(stockCache);
        VolumeWeightedStockPriceCalculator volumeWeightedStockPriceCalculator
                = new InMemoryVolumeWeightedStockPriceCalculator(globalBeverageExchange);
        GBCEAllSharesIndex allSharesIndex = new GBCEAllSharesIndex(stockCache,
                volumeWeightedStockPriceCalculator);
        ExchangeInvocationRestResource invocationRestResource =
                new ExchangeInvocationRestResource(stockCache, globalBeverageExchange,
                        volumeWeightedStockPriceCalculator, allSharesIndex);
        final ResourceConfig rc = new ResourceConfig();
        rc.register(invocationRestResource);
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();
        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        //noinspection ResultOfMethodCallIgnored
        System.in.read();
        server.stop();
    }
}
