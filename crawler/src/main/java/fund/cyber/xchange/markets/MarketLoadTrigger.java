package fund.cyber.xchange.markets;

import org.knowm.xchange.currency.CurrencyPair;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class MarketLoadTrigger implements Trigger {

    private static final int RATE = 10000;

    private final Market market;
    private final CurrencyPair pair;

    public MarketLoadTrigger(Market market, CurrencyPair pair) {
        this.market = market;
        this.pair = pair;
    }

    @Override
    public Date nextExecutionTime(TriggerContext context) {

        try {
            if (!market.getCurrencyPairs().contains(pair)) {
                return null;
            } else {
                if(context.lastActualExecutionTime() == null) {
                    return new Date();
                }
                Calendar next = Calendar.getInstance();
                if (context.lastActualExecutionTime() != null) {
                    next.setTime(context.lastActualExecutionTime());
                }
                next.add(Calendar.MILLISECOND, RATE);

                return next.getTime();
            }

        } catch (IOException e) {
            System.out.print("[4] Host: " + market.getExchange().getDefaultExchangeSpecification().getHost());
            System.out.print("Stop loading");
            System.out.println(e);
            return null;
        }
    }

}
