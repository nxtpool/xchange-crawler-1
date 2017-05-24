package fund.cyber.xchange;

import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class Main {

    public static void main(String[] args) {
        new ClassPathXmlApplicationContext(new String[]{"crawler.xml"});
    }

}