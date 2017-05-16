package fund.cyber.xchange.model.api;

import java.util.Date;

/**
 * Main REST service output DTO to display tickers
 * <p>
 * @author Andrey Lobarev nxtpool@gmail.com
 */
public class TickerDto {

    public Date timestamp;
    public String market;
    public String base;
    public String quote;
    public LastPriceDto last;
    public VolumeDto volume;

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMarket() {
        return market;
    }

    public void setMarket(String market) {
        this.market = market;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public LastPriceDto getLast() {
        return last;
    }

    public void setLast(LastPriceDto last) {
        this.last = last;
    }

    public VolumeDto getVolume() {
        return volume;
    }

    public void setVolume(VolumeDto volume) {
        this.volume = volume;
    }

}
