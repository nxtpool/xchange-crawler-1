package fund.cyber.xchange;

import fund.cyber.xchange.service.ChaingearDataLoader;
import fund.cyber.xchange.service.MarketDataLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Main {

    @Autowired
    private MarketDataLoader marketData;

    @Autowired
    private ChaingearDataLoader chaingearDataLoader;

    public static void main(String [] args) {
        new ClassPathXmlApplicationContext(new String[] {"crawler.xml"});
    }


}