package fund.cyber.xchange.markets;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Trade;

import java.util.function.BiConsumer;

public class TradeLoadTask implements Runnable {

    private final Market market;
    private final org.knowm.xchange.currency.CurrencyPair pair;
    private final BiConsumer<Trade, String> saver;

    public TradeLoadTask(Market market, CurrencyPair pair, BiConsumer<Trade, String> saver) {
        this.saver = saver;
        this.market = market;
        this.pair = pair;
    }

    @Override
    public void run() {
        try {
            market.loadTrades(pair, saver);
        } catch (java.io.IOException e) {
            System.out.print("[3] " + market.getClass().getSimpleName() + ":");
            System.out.println(e);
        }
    }
}
