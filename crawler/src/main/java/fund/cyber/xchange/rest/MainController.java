package fund.cyber.xchange.rest;


import fund.cyber.xchange.model.api.CalendarDto;
import fund.cyber.xchange.model.chaingear.Currency;
import fund.cyber.xchange.service.ChaingearDataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * REST service to display actual market tickers
 * <p>
 * @author Andrey Lobarev nxtpool@gmail.com
 */
@Controller
@RequestMapping(value = "/")
public class MainController {

    @Autowired
    private ChaingearDataLoader chaingearDataLoader;

    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public CalendarDto getTime() {
        return new CalendarDto(Calendar.getInstance());
    }

    @RequestMapping(value = "/chaingear", method = RequestMethod.GET)
    @ResponseBody
    public List<Currency> getChaingear() throws IOException {
        return chaingearDataLoader.loadCurrencies();
    }

    @RequestMapping(value = "/dictionary", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> getFiatCurrencies() throws IOException {
        return chaingearDataLoader.loadFiatCurrencies();
    }
}

