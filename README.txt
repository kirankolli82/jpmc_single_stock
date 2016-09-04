The exchange can be started via the class com.jpmc.gbc.exchange.ExchangeStartUp
No start up parameters are necessary.
This simple stock exchange's functions can be invoked via REST web calls (examples provided below).
The wadl for the services exposed can be accessed via http://localhost:8080/application.wadl

Assumptions:
1) One stock can be traded in a single currency.
2) Exchange only supports trading in USD and GBP currencies.
3) Volume Weighted Price for a stock is derived based on trades in the last 5 minutes. Which means
if no trade took place in the last 5 mins then no Volume Weighted Price will be retrieved.
4) All Shares Index will be calculated based on the constraints imposed by Point 3. Since multiple stocks
could be trading in USD or GBP the shares index is calculated separately for each currency. Ideally
currency conversion would be involved but for sake of simplicity it is being calculated separately.



The different functions and examples are as below:
1) Initialize stock data

    a) Create a common stock
    Example: http://localhost:8080/gbcexchange/stock/create/common?symbol=ABC&dividend=20&currency=USD
    Sample output: Common stock created : CommonStock{} Stock{stockIdentifier=StockIdentifier{stockSymbol='ABC', stockType=COMMON}, lastDividend=Price{price=20, currency=USD}}

    b) Create a preferred stock
    Example: http://localhost:8080/gbcexchange/stock/create/preferred?symbol=DEF&dividend=30&currency=USD&fixedDividendPercentage=10&parValue=100
    Sample Output: Preferred stock created : PreferredStock{dividendParValue=Price{price=10, currency=USD}} Stock{stockIdentifier=StockIdentifier{stockSymbol='DEF', stockType=PREFERRED}, lastDividend=Price{price=30, currency=USD}}

2) Execute trades

    a) Execute a trade
    Example: http://localhost:8080/gbcexchange/exchange/execute?symbol=ABC&stockType=COMMON&currency=USD&tradeSettlementPrice=100&direction=BUY&quantity=50
    Sample Output: Trade executed

    http://localhost:8080/gbcexchange/exchange/execute?symbol=ABC&stockType=COMMON&currency=USD&tradeSettlementPrice=80&direction=SELL&quantity=50

    http://localhost:8080/gbcexchange/exchange/execute?symbol=DEF&stockType=PREFERRED&currency=USD&tradeSettlementPrice=90&direction=SELL&quantity=180

    http://localhost:8080/gbcexchange/exchange/execute?symbol=ABC&stockType=COMMON&currency=USD&tradeSettlementPrice=80&direction=SELL&quantity=50

    http://localhost:8080/gbcexchange/exchange/execute?symbol=DEF&stockType=PREFERRED&currency=USD&tradeSettlementPrice=90&direction=SELL&quantity=180

3) Get Volume Weighted Price for a stock

    Example: http://localhost:8080/gbcexchange/heuristics/getVolumeWeightedStockPrice?symbol=ABC&stockType=COMMON
    Output: Volume Weight Stock Price:90 ,Currency:USD

    http://localhost:8080/gbcexchange/heuristics/getVolumeWeightedStockPrice?symbol=DEF&stockType=PREFERRED

    Output: Volume Weight Stock Price:90 ,Currency:USD

4) Get All Shares Index

    Example: http://localhost:8080/gbcexchange/index/getShareIndex
    Output: Index for: USD is 90

5) Get P/E Ratio

    Example: http://localhost:8080/gbcexchange/stock/getPERatio?symbol=ABC&stockType=COMMON&currency=USD&tradeSettlementPrice=100
    Output: P/E Ratio for StockIdentifier{stockSymbol='ABC', stockType=COMMON} is 5

6) Get Dividend Yield

    Example: http://localhost:8080/gbcexchange/stock/getDividendYield?symbol=ABC&stockType=COMMON&currency=USD&tradeSettlementPrice=100
    Output: Dividend yield for StockIdentifier{stockSymbol='ABC', stockType=COMMON} is Price{price=0.20000000, currency=USD}
