package fund.cyber.xchange.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import fund.cyber.xchange.markets.Market;
import fund.cyber.xchange.markets.OrderLoadTask;
import fund.cyber.xchange.markets.TradeLoadTask;
import fund.cyber.xchange.markets.MarketLoadTrigger;
import fund.cyber.xchange.model.api.OrderDto;
import fund.cyber.xchange.model.api.TradeDto;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.trade.LimitOrder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;

import java.util.Calendar;
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

    private static final int TRADE_RATE = 5000;
    private static final int ORDER_RATE = 15000;
    private static final int PAIRS_RATE = 60;

    private BiConsumer<Trade, String> tradeSaver = new BiConsumer<Trade, String>() {

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

    private BiConsumer<LimitOrder, String> orderSaver = new BiConsumer<LimitOrder, String>() {

        public void accept(LimitOrder order, String marketUrl) {
            OrderDto dto = new OrderDto(order, marketUrl);
            if (rethink) {
                dbService.insertOrder(dto);
            }
            if (elastic) {
                try {
                    elasticService.insertOrder(dto);
                } catch (JsonProcessingException e) {
                    System.out.println("Host: " + marketUrl + ". Pair: " + order.getCurrencyPair().base.getSymbol() + "/" + order.getCurrencyPair().counter.getSymbol());
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
                        System.out.print("[1] " + market.getClass().getSimpleName() + ":");
                        System.out.print("Remote init error");
                        System.out.println(e);

                    }
                }, context -> {
                    Calendar next = Calendar.getInstance();
                    if (context.lastActualExecutionTime() != null) {
                        next.setTime(context.lastActualExecutionTime());
                    }
                    next.add(Calendar.SECOND, PAIRS_RATE);
                    return next.getTime();
                });

            } catch (Exception e) {
                System.out.print("[2] " + market.getClass().getSimpleName() + ":");
                System.out.print("Ignore market");
                System.out.println(e);

            }

            try {
                for (CurrencyPair pair : market.getCurrencyPairs()) {
                    ScheduledFuture<?> s = scheduler.schedule(new TradeLoadTask(market, pair, tradeSaver), new MarketLoadTrigger(market, pair, TRADE_RATE));
                }
            } catch (Exception e) {
                System.out.print("[6] " + market.getClass().getSimpleName() + ":");
                System.out.print("Ignore market");
                System.out.println(e);

            }

            try {
                for (CurrencyPair pair : market.getCurrencyPairs()) {
                    ScheduledFuture<?> s = scheduler.schedule(new OrderLoadTask(market, pair, orderSaver), new MarketLoadTrigger(market, pair, ORDER_RATE));
                }
            } catch (Exception e) {
                System.out.print("[6] " + market.getClass().getSimpleName() + ":");
                System.out.print("Ignore market");
                System.out.println(e);

            }
        }
    }

}
