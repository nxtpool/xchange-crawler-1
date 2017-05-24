package fund.cyber.xchange.markets;

import org.knowm.xchange.currency.CurrencyPair;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class MarketLoadTrigger implements Trigger {

    private static final int RATE = 5000;

    private final Market market;
    private final CurrencyPair pair;

    public MarketLoadTrigger(Market market, CurrencyPair pair) {
        this.market = market;
        this.pair = pair;
    }

    @Override
    public Date nextExecutionTime(TriggerContext context) {
        //This is not interesting because of Async
        /*
        System.out.println(String.format("Market:%s, Pair:%s, ScheduledTime:%3$tT.%3$tL, ExecutionTime:%4$tT.%4$tL, CompletionTime:%5$tT.%5$tL",
                market.getMarketUrl(),
                pair.toString(),
                context.lastScheduledExecutionTime(),
                context.lastActualExecutionTime(),
                context.lastCompletionTime()));
                */
        try {
            if (!market.getCurrencyPairs().contains(pair)) {
                return null;
            } else {
                if(context.lastActualExecutionTime() == null) {
                    return new Date();
                }
                Calendar next = Calendar.getInstance();
                next.setTime(context.lastActualExecutionTime());
                next.add(Calendar.MILLISECOND, RATE);
                /*
                System.out.println(String.format("Market:%s, Pair:%s, Next call:%3$tT.%3$tL",
                        market.getMarketUrl(),
                        pair.toString(),
                        next));
*/

                return next.getTime();
            }

        } catch (IOException e) {
            System.out.print("Host: " + market.getExchange().getDefaultExchangeSpecification().getHost());
            System.out.print("Stop loading");
            System.out.println(e);
            return null;
        }
    }

}
