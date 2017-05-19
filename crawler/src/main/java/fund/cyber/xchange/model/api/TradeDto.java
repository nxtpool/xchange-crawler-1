package fund.cyber.xchange.model.api;

import org.knowm.xchange.dto.marketdata.Trade;

import java.math.BigDecimal;
import java.util.Date;

public class TradeDto {

    public final String type;
    public final BigDecimal baseAmount;
    public final BigDecimal quoteAmount;
    public final BigDecimal price;
    public final Date timestamp;
    public final String id;
    public final String market;
    public String base;
    public String quote;

    public TradeDto(Trade trade, String market) {

        this.type = trade.getType().name();
        this.baseAmount = trade.getTradableAmount();
        this.base = trade.getCurrencyPair().base.getSymbol();
        this.quote = trade.getCurrencyPair().counter.getSymbol();
        this.price = trade.getPrice();
        this.quoteAmount = trade.getTradableAmount().multiply(trade.getPrice());
        this.timestamp = trade.getTimestamp();
        this.id = trade.getId();
        this.market = market;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getBaseAmount() {
        return baseAmount;
    }

    public BigDecimal getQuoteAmount() {
        return quoteAmount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getId() {
        return id;
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

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        return this.hashCode() == o.hashCode();
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    @Override
    public String toString() {

        return "Trade [type=" + type + ", baseAmount=" + baseAmount + ", currencyPair=" + base + "/" + quote + ", price=" + price + ", timestamp="
                + timestamp + ", id=" + id + "]";
    }

}

