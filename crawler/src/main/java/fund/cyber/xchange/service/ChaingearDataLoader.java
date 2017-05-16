package fund.cyber.xchange.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.knowm.xchange.currency.CurrencyPair;
import org.knowm.xchange.dto.marketdata.Ticker;
import fund.cyber.xchange.model.api.LastPriceDto;
import fund.cyber.xchange.model.api.TickerDto;
import fund.cyber.xchange.model.api.VolumeDto;
import fund.cyber.xchange.model.chaingear.Currency;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Chaingear project Data loader. Used to have standardized names for currencies. And filter out 'trash' coins.
 * <p>
 * @author Andrey Lobarev nxtpool@gmail.com
 */
@Component
public class ChaingearDataLoader implements InitializingBean {

    public static final String CHAINGEAR_URL = "http://chaingear.cyber.fund/chaingear.json";

    private Map<String, String> currencyNames;

    private Map<String, String> fiatCurrencies = new HashMap<>();

    @Value("${ignore.symbols}")
    private String ignoreSymbolsString;

    @Value("${rename.symbols}")
    private String renameSymbolsString;

    private Set<String> ignoreSymbols;
    private Map<String, String> renameSymbols;


    public List<Currency> loadCurrencies() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        URL url = new URL(CHAINGEAR_URL);

        return objectMapper.readValue(url, objectMapper.getTypeFactory().
                constructCollectionType(List.class, Currency.class));
    }

    public Map<String, String> loadFiatCurrencies() throws IOException {
        return fiatCurrencies;
    }

    public Map<String, String> loadCurrencyNames() throws IOException {
        return loadCurrencies().stream().collect(Collectors.toMap(
                currency -> currency.getToken().getSymbol(),
                Currency::getSystem,
                (first, last) -> last));
    }

    public String getCurrencyName(String symbol) {
        return currencyNames.get(symbol);
    }

    public boolean isCurrency(String code) {
        return (java.util.Currency.getAvailableCurrencies().stream()
                .anyMatch(currency -> currency.getCurrencyCode().equals(code)) && !(ignoreSymbols.contains(code)))
                || currencyNames.containsKey(code);
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        currencyNames = loadCurrencyNames();
        ignoreSymbols = Arrays.stream(ignoreSymbolsString.split(",")).map(s -> s.trim()).collect(Collectors.toSet());
        renameSymbols = (new ObjectMapper()).readValue(renameSymbolsString, new TypeReference<Map<String,String>>(){});
    }

    private String getNameOrLeaveSymbol(String symbol) {
        String name = getCurrencyName(symbol);
        if (name != null) {
            return name;
        }
        try {
            if (ignoreSymbols.contains(symbol)) {
                return "Error:" + symbol;
            }
            if (renameSymbols.keySet().contains(symbol)) {
                return renameSymbols.get(symbol);
            }
            String fiatName = java.util.Currency.getInstance(symbol).getDisplayName();
            fiatCurrencies.put(symbol, fiatName);
            return fiatName;
        } catch (IllegalArgumentException ex) {
            return "Error:" + symbol;
        }
    }

    public TickerDto createTickerDto(Ticker ticker, CurrencyPair pair, String market) {
        TickerDto dto = new TickerDto();
        dto.setTimestamp(ticker.getTimestamp());
        dto.setMarket(market);
        dto.setBase(getNameOrLeaveSymbol(pair.counter.getSymbol()));
        dto.setQuote(getNameOrLeaveSymbol(pair.base.getSymbol()));
        LastPriceDto price = new LastPriceDto();
        price.setNativePrice(ticker.getLast());
        dto.setLast(price);
        VolumeDto volume = new VolumeDto();
        volume.setNativeVolume(ticker.getVolume());
        dto.setVolume(volume);
        return dto;
    }
}
