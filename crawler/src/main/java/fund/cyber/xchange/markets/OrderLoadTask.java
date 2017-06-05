package fund.cyber.xchange.markets;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.trade.LimitOrder;

import java.util.function.BiConsumer;

public class OrderLoadTask implements Runnable {

    private final Market market;
    private final CurrencyPair pair;
    private final BiConsumer<LimitOrder, String> saver;

    public OrderLoadTask(Market market, CurrencyPair pair, BiConsumer<LimitOrder, String> saver) {
        this.saver = saver;
        this.market = market;
        this.pair = pair;
    }

    @Override
    public void run() {
        try {
            market.loadOrders(pair, saver);
        } catch (java.io.IOException e) {
            System.out.print("[2] " + market.getClass().getSimpleName() + ":");
            System.out.println(e);
        }
    }
}
