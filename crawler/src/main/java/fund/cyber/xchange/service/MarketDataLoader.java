package fund.cyber.xchange.service;

import fund.cyber.xchange.markets.Market;
import fund.cyber.xchange.markets.Markets;
import fund.cyber.xchange.model.common.IndexHolder;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

@Service
public class MarketDataLoader implements InitializingBean {

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

    @Scheduled(fixedRate = 2000)
    protected void loadData() throws IOException {
        taskExecutor.execute(() -> {
            Market market = getNextMarket();

            Calendar start = Calendar.getInstance();

            try {
                market.loadData();
            } catch (IOException e) {
                System.out.print("Host: " + market.getExchange().getDefaultExchangeSpecification().getHost());
                System.out.println(e);
            }

        });
    }

}
