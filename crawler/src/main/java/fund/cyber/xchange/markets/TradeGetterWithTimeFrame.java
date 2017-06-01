package fund.cyber.xchange.markets;


import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.dto.marketdata.Trades;
import org.knowm.xchange.poloniex.service.PoloniexMarketDataService;
import org.knowm.xchange.service.marketdata.MarketDataService;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Trade history getter for exchange supporting since and till time parameters
 * <p>
 *
 * @author Andrey Lobarev nxtpool@gmail.com
 */
public class TradeGetterWithTimeFrame extends TradeGetterDefault {
    public TradeGetterWithTimeFrame(MarketDataService dataService) {
        super(dataService);
    }

    @Override
    public List<Trade> apply(CurrencyPair pair, Trade lastTrade) {
        if (lastTrade == null) {
            return super.apply(pair, null);
        }
        try {
            long start = lastTrade.getTimestamp().getTime() / 1000 - 1;
            long end = new Date().getTime() / 1000 + 60;
            Trades trades = null;
            trades = ((PoloniexMarketDataService)dataService).getTrades(pair,start, end);
            return trades.getTrades();
        } catch (IOException e) {
            System.out.println(String.format("%tT Error in trade getter with time frame. Pair: %s, with id.",
                    new Date(), pair, lastTrade.getId()));
            return null;
        }
    }
}
