package fund.cyber.xchange.markets;

import fund.cyber.xchange.service.ElasticsearchService;
import fund.cyber.xchange.service.RethinkDbService;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.exceptions.NotAvailableFromExchangeException;
import org.knowm.xchange.exceptions.NotYetImplementedForExchangeException;
import org.knowm.xchange.service.marketdata.MarketDataService;
import fund.cyber.xchange.model.api.TickerDto;
import fund.cyber.xchange.service.ChaingearDataLoader;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Abstract Market Service
 * <p>
 * @author Andrey Lobarev nxtpool@gmail.com
 */
public abstract class Market implements InitializingBean {

    protected Exchange exchange;

    protected MarketDataService dataService;

    @Autowired
    private ChaingearDataLoader chaingearDataLoader;

    @Autowired
    private RethinkDbService dbService;

    @Autowired
    private ElasticsearchService elasticService;

    public abstract Class<? extends Exchange> getExchangeClass();

    @Override
    public void afterPropertiesSet() throws Exception {
        initExchange();
        dataService = exchange.getMarketDataService();
    }

    public void initExchange() {
        exchange = ExchangeFactory.INSTANCE.createExchange(getExchangeClass().getName());
        exchange.applySpecification(exchange.getDefaultExchangeSpecification());
    }

    public boolean useCurrentDate() {
        return false;
    }

    public List<CurrencyPair> getCurrencyPairs() throws IOException {
        return exchange.getExchangeSymbols();
    }

    public List<Trade> getTrades(CurrencyPair currencyPair) throws NotAvailableFromExchangeException, NotYetImplementedForExchangeException, IOException {
        return dataService.getTrades(currencyPair).getTrades();
    }

    public Ticker getTicker(CurrencyPair currencyPair) throws IOException {
        Ticker ticker = dataService.getTicker(currencyPair);
        if (ticker == null) {
            return null;
        }

        if (ticker.getTimestamp() != null && ticker.getTimestamp().after(new Date(0L))) {
            return ticker;
        }

        List<Trade> trades = new ArrayList<>();
        try {
            trades = getTrades(currencyPair);
        } catch (NotAvailableFromExchangeException | NotYetImplementedForExchangeException e) {
            //Do nothing
        }

        if (trades.size() == 0 && !useCurrentDate()) {
            return null;
        }

        trades.sort((o1, o2) -> o2.getTimestamp().compareTo(o1.getTimestamp()));
        Date date = (trades.size() > 0) ? trades.get(0).getTimestamp() : new Date();

        return (new Ticker.Builder()).currencyPair(currencyPair)
                .last(ticker.getLast())
                .bid(ticker.getBid())
                .ask(ticker.getAsk())
                .high(ticker.getHigh())
                .low(ticker.getLow())
                .vwap(ticker.getVwap())
                .volume(ticker.getVolume())
                .timestamp(date).build();
    }

    public String getMarketUrl() {
        String url = exchange.getDefaultExchangeSpecification().getHost() != null ?
                exchange.getDefaultExchangeSpecification().getHost() :
                exchange.getDefaultExchangeSpecification().getSslUri();
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            url = "http://" + url;
        }
        return url;
    }

    public void loadData() throws IOException {
        List<TickerDto> tickers = new ArrayList<TickerDto>();
        for (CurrencyPair pair : getCurrencyPairs()) {
            if (!chaingearDataLoader.isCurrency(pair.counter.getSymbol()) || !chaingearDataLoader.isCurrency(pair.base.getSymbol())) {
                continue;
            }
            try {
                Ticker ticker = getTicker(pair);
                Calendar yesterday = Calendar.getInstance();
                yesterday.add(Calendar.DAY_OF_MONTH, -1);
                if (ticker != null && ticker.getTimestamp().after(yesterday.getTime())) {
                    TickerDto tickerDto = chaingearDataLoader.createTickerDto(ticker, pair, getMarketUrl());
                    dbService.insertTicker(tickerDto);
                    elasticService.insertTicker(tickerDto);
                }
            } catch (IOException e) {
                System.out.print("Host: " + exchange.getDefaultExchangeSpecification().getHost() + ". Pair: " + pair.base.getSymbol() + "/" + pair.counter.getSymbol());
                System.out.println(e);
            }
        }
    }

    public Exchange getExchange() {
        return exchange;
    }
}