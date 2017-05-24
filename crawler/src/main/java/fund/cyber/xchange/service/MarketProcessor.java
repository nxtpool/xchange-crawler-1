package fund.cyber.xchange.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fund.cyber.xchange.markets.Market;
import fund.cyber.xchange.markets.TradeLoadTask;
import fund.cyber.xchange.markets.MarketLoadTrigger;
import fund.cyber.xchange.model.api.TradeDto;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Trade;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.function.BiConsumer;

@Service
public class MarketProcessor implements InitializingBean {

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

    @Autowired
    private TaskScheduler scheduler;

    private BiConsumer<Trade, String> saver = new BiConsumer<Trade, String>() {

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
    };

    @Override
    public void afterPropertiesSet() {
        for (Market market : markets) {
            try {
                for (CurrencyPair pair : market.getCurrencyPairs()) {
                    ScheduledFuture<?> s = scheduler.schedule(new TradeLoadTask(market, pair, saver), new MarketLoadTrigger(market, pair));
                }
            } catch (Exception e) {
                System.out.print("Host: " + market.getExchange().getDefaultExchangeSpecification().getHost());
                System.out.print("Ignore market");
                System.out.println(e);

            }
        }
    }

}
