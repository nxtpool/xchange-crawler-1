package fund.cyber.xchange.markets;

import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Trade;

import java.util.List;
import java.util.function.BiFunction;

/**
 * Trade history getter
 * <p>
 *
 * @author Andrey Lobarev nxtpool@gmail.com
 */
public interface TradeGetter extends BiFunction<CurrencyPair, Trade, List<Trade>> {

}
