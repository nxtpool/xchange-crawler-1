package fund.cyber.xchange.model.chaingear;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Token DTO. Contains trade symbol.
 * <p>
 * @author Andrey Lobarev nxtpool@gmail.com
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Token {
    private String name;
    private String symbol;
    private String base;
    private String main;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }
}
