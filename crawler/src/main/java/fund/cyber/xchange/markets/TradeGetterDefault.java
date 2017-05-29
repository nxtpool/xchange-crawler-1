package fund.cyber.xchange.markets;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.service.marketdata.MarketDataService;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Default trade history getter
 * <p>
 *
 * @author Andrey Lobarev nxtpool@gmail.com
 */
public class TradeGetterDefault implements TradeGetter {

    protected final MarketDataService dataService;

    public TradeGetterDefault(MarketDataService dataService) {
        this.dataService = dataService;
    }

    @Override
    public List<Trade> apply(CurrencyPair pair, Trade lastTrade) {
        try {
            return dataService.getTrades(pair).getTrades();
        } catch (IOException e) {
            System.out.println(String.format("%tT Error in default trade getter. Pair: %s, with id %s. Data service: %s\n",
                    new Date(), pair, lastTrade != null ? lastTrade.getId() : "null", dataService.getClass().getName()) + e);
            return null;
        }
    }

}
