package fund.cyber.xchange.model.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fund.cyber.xchange.model.common.BigDecimalSerializer;

import java.math.BigDecimal;

/**
 * Price DTO
 * <p>
 * @author Andrey Lobarev nxtpool@gmail.com
 */
public class LastPriceDto {
    @JsonProperty("native")
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal nativePrice;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonSerialize(using = BigDecimalSerializer.class)
    private BigDecimal usd;

    @JsonSerialize(using = BigDecimalSerializer.class)
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BigDecimal btc;

    public BigDecimal getNativePrice() {
        return nativePrice;
    }

    public void setNativePrice(BigDecimal nativePrice) {
        this.nativePrice = nativePrice;
    }

    public BigDecimal getUsd() {
        return usd;
    }

    public void setUsd(BigDecimal usd) {
        this.usd = usd;
    }

    public BigDecimal getBtc() {
        return btc;
    }

    public void setBtc(BigDecimal btc) {
        this.btc = btc;
    }
}
