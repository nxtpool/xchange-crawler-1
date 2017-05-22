package fund.cyber.xchange.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fund.cyber.xchange.markets.Market;
import fund.cyber.xchange.model.api.TradeDto;
import fund.cyber.xchange.model.common.IndexHolder;
import org.knowm.xchange.dto.marketdata.Trade;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.function.BiConsumer;

@Service
public class MarketDataLoader implements InitializingBean {

    @Value("${elastic.save}")
    private boolean elastic;

    @Value("${rethink.save}")
    private boolean rethink;

    @Autowired
    private RethinkDbService dbService;

    @Autowired
    private ElasticsearchService elasticService;

    @Autowired
    private List<Market> markets;

    private final IndexHolder indexHolder = new IndexHolder();

    @Override
    public void afterPropertiesSet() throws Exception {
        indexHolder.setLength(markets.size());
    }

    private Market getNextMarket() {
        synchronized (indexHolder) {
            Market market = markets.get(indexHolder.getIndex());
            indexHolder.increaseIndex();
            return market;
        }
    }

    @Autowired
    private TaskExecutor taskExecutor;

    @Scheduled(fixedRate = 1000)
    protected void loadData() throws IOException {
        taskExecutor.execute(() -> {
            Market market = getNextMarket();

            Calendar start = Calendar.getInstance();

            try {
                market.loadTrades(new MarketTradeSaver());
            } catch (IOException e) {
                System.out.print("Host: " + market.getExchange().getDefaultExchangeSpecification().getHost());
                System.out.println(e);
            }

        });
    }

    public class MarketTradeSaver implements BiConsumer<Trade, String> {

        @Override
        public void accept(Trade trade, String marketUrl) {
            TradeDto dto = new TradeDto(trade, marketUrl);
            if (rethink) {
                dbService.insertTrade(dto);
            }
            if (elastic) {
                try {
                    elasticService.insertTrade(dto);
                } catch (JsonProcessingException e) {
                    System.out.println("Host: " + marketUrl + ". Pair: " + trade.getCurrencyPair().base.getSymbol() + "/" + trade.getCurrencyPair().counter.getSymbol());
                    System.out.println(e);
                }
            }
        }
    }

}
