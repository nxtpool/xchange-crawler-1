package fund.cyber.xchange.markets;

import org.knowm.xchange.currency.CurrencyPair;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class MarketLoadTrigger implements Trigger {

    private final Market market;
    private final CurrencyPair pair;
    private final int rate;

    public MarketLoadTrigger(Market market, CurrencyPair pair, int rate) {
        this.market = market;
        this.pair = pair;
        this.rate =  rate;
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
                next.setTime(context.lastActualExecutionTime());
                next.add(Calendar.MILLISECOND, rate);

                return next.getTime();
            }

        } catch (IOException e) {
            System.out.print("[4] " + market.getClass().getSimpleName() + ":");
            System.out.print("Stop loading");
            System.out.println(e);
            return null;
        }
    }

}
