package fund.cyber.xchange.markets;

import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.Order;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.service.marketdata.MarketDataService;
import fund.cyber.xchange.service.ChaingearDataLoader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collector;
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

    private Map<CurrencyPair,Trade> lastReceivedTrade = new HashMap<>();

    @Override
    public void afterPropertiesSet() throws Exception {
        try {
            exchange = ExchangeFactory.INSTANCE.createExchange(getExchangeClass().getName());
            dataService = exchange.getMarketDataService();
            initMarketUrl();
        } catch (Throwable t) {
            System.out.println(t);
        }
    }

    public abstract Class<? extends Exchange> getExchangeClass();

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

    public List<Trade> getTrades(CurrencyPair pair) throws NotAvailableFromExchangeException, NotYetImplementedForExchangeException, IOException {
        List<Trade> trades = dataService.getTrades(pair).getTrades();
        trades = trades.stream().map(trade -> trade.getId() != null ? trade :
                new Trade(trade.getType(), trade.getTradableAmount(), trade.getCurrencyPair(),
                        trade.getPrice(), trade.getTimestamp(), trade.toString()))
                .collect(Collectors.toList());
        trades.sort(Comparator.comparing(Trade::getTimestamp));
        if (lastReceivedTrade.get(pair) != null) {
            int index = trades.indexOf(lastReceivedTrade.get(pair));
            if (index > 0) {
                trades = trades.subList(0, index);
            } else {
                //TODO need to request more trades
                System.out.println("Not enough data. Host: " + marketUrl + ". Pair: " + pair.base.getSymbol() + "/" + pair.counter.getSymbol());
            }
        }
        if (trades.size() > 0) {
            lastReceivedTrade.put(pair, trades.get(trades.size() - 1));
        } else {
            System.out.println("No data. Host: " + marketUrl + ". Pair: " + pair.base.getSymbol() + "/" + pair.counter.getSymbol());
        }
        return trades;
    }

    @Async
    public void loadTrades(BiConsumer<Trade, String> tradeSaver) throws IOException {
        for (CurrencyPair pair : getCurrencyPairs()) {
            loadTrades(pair, tradeSaver);
        }
    }

    @Async
    public void loadTrades(CurrencyPair pair, BiConsumer<Trade, String> tradeSaver) throws IOException {
            if (!chaingearDataLoader.isCurrency(pair.counter.getSymbol()) || !chaingearDataLoader.isCurrency(pair.base.getSymbol())) {
                return;
            }
            try {
                for (Trade trade : getTrades(pair)) {
                    tradeSaver.accept(trade, marketUrl);
                }
            } catch (IOException e) {
                System.out.println("Host: " + exchange.getDefaultExchangeSpecification().getHost() + ". Pair: " + pair.base.getSymbol() + "/" + pair.counter.getSymbol());
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