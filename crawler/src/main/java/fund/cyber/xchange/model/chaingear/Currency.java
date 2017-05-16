package fund.cyber.xchange.model.chaingear;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Main chaingear input DTO. We need only few fields to create map of necessary currencies.
 * <p>
 * @author Andrey Lobarev nxtpool@gmail.com
 */
@JsonIgnoreProperties(value = {"aliases"}, ignoreUnknown = true)
public class Currency {
    private String system;
    private Token token;

    private Aliases aliases;

    public Aliases getAliases() {
        return aliases;
    }

    public void setAliases(Aliases aliases) {
        this.aliases = aliases;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
