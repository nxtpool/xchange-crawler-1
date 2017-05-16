package fund.cyber.xchange.model.chaingear;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Aliases DTO. It seems it's not needed anymore.
 * <p>
 * @author Andrey Lobarev nxtpool@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Aliases {

    private String coinmarketcap;
    private String CurrencyName;
    private String nickname;

    public String getCoinmarketcap() {
        return coinmarketcap;
    }

    public void setCoinmarketcap(String coinmarketcap) {
        this.coinmarketcap = coinmarketcap;
    }

    public String getCurrencyName() {
        return CurrencyName;
    }

    public void setCurrencyName(String currencyName) {
        CurrencyName = currencyName;
    }
}
