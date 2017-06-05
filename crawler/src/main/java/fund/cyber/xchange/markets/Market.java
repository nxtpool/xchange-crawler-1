package fund.cyber.xchange.markets;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.OrderBook;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.service.marketdata.MarketDataService;
import fund.cyber.xchange.service.ChaingearDataLoader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * Abstract Market Service
 * <p>
 *
 * @author Andrey Lobarev nxtpool@gmail.com
 */
public abstract class Market implements InitializingBean {

    @Autowired
    private ChaingearDataLoader chaingearDataLoader;

    private Exchange exchange;
    private MarketDataService dataService;

    private String marketUrl;

    private List<CurrencyPair> currencyPairs;
    private Calendar currencyPairsLastRequest;

    private Map<CurrencyPair,List<Trade>> lastTrades = new HashMap<>();

    protected TradeGetter tradeGetter;

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            exchange = ExchangeFactory.INSTANCE.createExchange(getExchangeClass().getName());
            dataService = exchange.getMarketDataService();
            initTradeGetter();
            initMarketUrl();
        } catch (Throwable t) {
            System.out.println(t);
        }
    }

    public abstract Class<? extends Exchange> getExchangeClass();

    public void initTradeGetter() {
        tradeGetter = new TradeGetterDefault(dataService);
    }

    private void initMarketUrl() {
        marketUrl = exchange.getDefaultExchangeSpecification().getHost();
        if (marketUrl == null) {
            marketUrl = exchange.getDefaultExchangeSpecification().getSslUri();
        }
        if (marketUrl == null) {
            marketUrl = exchange.getDefaultExchangeSpecification().getPlainTextUri();
        }
        if (!marketUrl.startsWith("http://") && !marketUrl.startsWith("https://")) {
            marketUrl = "http://" + marketUrl;
        }
    }

    public List<CurrencyPair> getCurrencyPairs() throws IOException {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.MINUTE, -10);
        if (currencyPairsLastRequest == null || currencyPairsLastRequest.before(now)) {
            currencyPairs = exchange.getExchangeSymbols();
            currencyPairsLastRequest = Calendar.getInstance();
        }
        return currencyPairs;
    }

    protected List<CurrencyPair> getExchangeSymbols() {
        return exchange.getExchangeSymbols();
    }

    public void updateMarketPairs() throws IOException {
        exchange.remoteInit();
    }

    public List<Trade> getTrades(CurrencyPair pair) throws NotAvailableFromExchangeException, NotYetImplementedForExchangeException, IOException {
        Calendar start = Calendar.getInstance();
        List<Trade> lastPairTrades = lastTrades.get(pair);
        Trade lastTrade = lastPairTrades != null ? lastPairTrades.get(lastPairTrades.size() - 1) : null;
        List<Trade> trades = tradeGetter.apply(pair, lastTrade);
        if (trades == null) {
            //FIXME maybe need something to do here
            return new ArrayList<>();
        }
        trades.sort(Comparator.comparing(Trade::getTimestamp));
        trades = trades.stream().map(trade -> trade.getId() != null ? trade : new Trade(trade.getType(),
                trade.getTradableAmount(), trade.getCurrencyPair(), trade.getPrice(), trade.getTimestamp(),
                "" + trade.toString().hashCode())).collect(Collectors.toList());
        if (trades.size() == 0) {
            //System.out.println((new Date().toString()) + " No data. Host: " + marketUrl + ". Pair: " + pair.base.getSymbol() + "/" + pair.counter.getSymbol());
            return trades;
        }

        lastTrades.put(pair, trades);

        if (lastTrade != null) {
            int index = trades.indexOf(lastTrade);
            if (index > -1) {
                trades = trades.subList(index + 1, trades.size());
                //System.out.println(trades.size());
            } else if (Collections.disjoint(lastPairTrades, trades)) {
                System.out.println(String.format("%tT Not enough data. Host: %s. Pair: %s. Search for id: %s. Last id: %s.",
                        new Date(), marketUrl, pair, lastTrade.getId(), trades.get(trades.size() - 1).getId()));
            }
        }

        return trades;
    }

    public List<LimitOrder> getOrders(CurrencyPair pair) throws NotAvailableFromExchangeException, NotYetImplementedForExchangeException, IOException {
        Calendar now = Calendar.getInstance();
        List<LimitOrder> orders = new ArrayList<>();
        try {
            OrderBook orderBook = dataService.getOrderBook(pair);
            orders = orderBook.getAsks();
            orders.addAll(orderBook.getBids());
        } catch (NotYetImplementedForExchangeException e) {
            return orders;
        }
        orders = orders.stream().map(order -> order.getTimestamp() != null ? order :
                new LimitOrder(order.getType(), order.getTradableAmount(), order.getCurrencyPair(),
                        order.getId(),  now.getTime(), order.getLimitPrice(), order.getAveragePrice(),
                        order.getCumulativeAmount(), order.getStatus()))
                .collect(Collectors.toList());
        orders.sort(Comparator.comparing(LimitOrder::getLimitPrice));

        if (orders.size() == 0) {
            System.out.println((new Date().toString()) + " No data. Host: " + marketUrl + ". Pair: " + pair.base.getSymbol() + "/" + pair.counter.getSymbol());
        }

        return orders;
    }

    @Async
    public void processTrades(CurrencyPair pair, BiConsumer<Trade, String> tradeSaver) throws IOException {
            if (!chaingearDataLoader.isCurrency(pair.counter.getSymbol()) || !chaingearDataLoader.isCurrency(pair.base.getSymbol())) {
                return;
            }
            try {
                for (Trade trade : getTrades(pair)) {
                    tradeSaver.accept(trade, marketUrl);
                }
            } catch (SocketTimeoutException ste) {
                processTrades(pair, tradeSaver);
            } catch (IOException e) {
                System.out.println("[5] Host: " + marketUrl + ". Pair: " + pair.base.getSymbol() + "/" + pair.counter.getSymbol());
                System.out.println(e);
            }
    }

    @Async
    public void loadOrders(CurrencyPair pair, BiConsumer<LimitOrder, String> orderSaver) throws IOException {
        if (!chaingearDataLoader.isCurrency(pair.counter.getSymbol()) || !chaingearDataLoader.isCurrency(pair.base.getSymbol())) {
            return;
        }
        try {
            for (LimitOrder order : getOrders(pair)) {
                orderSaver.accept(order, marketUrl);
            }
        } catch (IOException e) {
            System.out.println("[5] Host: " + marketUrl + ". Pair: " + pair.base.getSymbol() + "/" + pair.counter.getSymbol());
            System.out.println(e);
        }
    }

    public Exchange getExchange() {
        return exchange;
    }

    public MarketDataService getDataService() {
        return dataService;
    }

    public String getMarketUrl() {
        return marketUrl;
    }
}