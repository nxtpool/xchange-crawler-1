package fund.cyber.xchange.markets;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Trade;
import org.knowm.xchange.service.marketdata.MarketDataService;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Trade history getter for exchange supporting since id parameter
 * <p>
 *
 * @author Andrey Lobarev nxtpool@gmail.com
 */
public class TradeGetterWithSinceParam extends TradeGetterDefault {

    public TradeGetterWithSinceParam(MarketDataService dataService) {
        super(dataService);
    }

    @Override
    public List<Trade> apply(CurrencyPair pair, Trade lastTrade) {
        if (lastTrade == null) {
            return super.apply(pair, null);
        }
        try {
            return loadTradesFromTill(pair, lastTrade.getId(), null);
        } catch (IOException e) {
            System.out.println(String.format("%tT Error in trade getter with since param. Pair: %s, with id.",
                    new Date(), pair, lastTrade.getId()));
            return null;
        }
    }

    private List<Trade> loadTradesFromTill(CurrencyPair pair, String from, String till) throws IOException {
        List<Trade> trades = till == null ? super.apply(pair, null) :
                dataService.getTrades(pair, till).getTrades();

        if (trades.stream().anyMatch(trade -> trade.getId().equals(from))) {
            return trades;
        } else {
            List<Trade> result = loadTradesFromTill(pair, from, trades.get(0).getId());
            result.addAll(trades);
            return result;
        }
    }
}
