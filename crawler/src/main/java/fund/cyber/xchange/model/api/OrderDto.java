package fund.cyber.xchange.model.api;

import org.knowm.xchange.dto.trade.LimitOrder;

import java.math.BigDecimal;
import java.util.Date;

public class OrderDto {

    public final String type;
    public final BigDecimal baseAmount;
    public final BigDecimal quoteAmount;
    public final BigDecimal limitPrice;
    public final Date timestamp;
    public final String market;
    public String base;
    public String quote;

    public OrderDto(LimitOrder trade, String market) {
        this.type = trade.getType() != null ? trade.getType().name() : null;
        this.baseAmount = trade.getTradableAmount();
        this.base = trade.getCurrencyPair().base.getSymbol();
        this.quote = trade.getCurrencyPair().counter.getSymbol();
        this.limitPrice = trade.getLimitPrice();
        this.quoteAmount = trade.getTradableAmount().multiply(trade.getLimitPrice());
        this.timestamp = trade.getTimestamp();
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

    public BigDecimal getLimitPrice() {
        return limitPrice;
    }

    public Date getTimestamp() {
        return timestamp;
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

    public String getMarket() {
        return market;
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

        return "LimitOrder [type=" + type + ", baseAmount=" + baseAmount + ", currencyPair=" + base + "/" + quote + ", limitPrice=" + limitPrice + ", timestamp="
                + timestamp + "]";
    }

}

