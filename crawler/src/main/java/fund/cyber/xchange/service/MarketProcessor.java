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
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.function.BiConsumer;

@Service
public class MarketProcessor implements InitializingBean {

    @Autowired
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Config config;

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

            boolean elastic = config.getProperty("elastic.save").equals("true");
            boolean rethink = config.getProperty("rethink.save").equals("true");

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

                scheduler.schedule(() -> {
                    try {
                        market.updateMarketPairs();
                    } catch (Exception e) {
                        System.out.print("[1] Host: " + market.getExchange().getDefaultExchangeSpecification().getHost());
                        System.out.print("Remote init error");
                        System.out.println(e);

                    }
                }, context -> {
                    Calendar next = Calendar.getInstance();
                    if (context.lastActualExecutionTime() != null) {
                        next.setTime(context.lastActualExecutionTime());
                    }
                    next.add(Calendar.SECOND, 60);
                    return next.getTime();
                });

            } catch (Exception e) {
                System.out.print("[2] Host: " + market.getExchange().getDefaultExchangeSpecification().getHost());
                System.out.print("Ignore market");
                System.out.println(e);

            }
            try {
                for (CurrencyPair pair : market.getCurrencyPairs()) {
                    TradeLoadTask task = new TradeLoadTask(market, pair, saver);
                    scheduler.schedule(task, new MarketLoadTrigger(market, pair));
                }
            } catch (Exception e) {
                System.out.print("[6] Host: " + market.getExchange().getDefaultExchangeSpecification().getHost());
                System.out.print("Ignore market");
                System.out.println(e);

            }
        }
    }

}
