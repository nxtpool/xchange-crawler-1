package fund.cyber.xchange.service;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.model.MapObject;
import com.rethinkdb.net.Connection;
import fund.cyber.xchange.model.api.TradeDto;
import fund.cyber.xchange.model.api.TickerDto;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Date;

@Component
public class RethinkDbService implements InitializingBean {

    private static final String TICKERS = "tickers";
    private static final String TRADES = "trades";
    private RethinkDB r;
    private Connection conn;

    @Value("${rethink.db}")
    private String db;

    @Value("${rethink.host}")
    private String host;

    @Value("${rethink.port}")
    private int port;

    @Value("${rethink.authKey}")
    private String authKey;

    @Override
    public void afterPropertiesSet() throws Exception {
        r = RethinkDB.r;
        Connection.Builder connBuilder = r.connection().hostname(host).port(port);
        if (authKey.trim().length() > 0) {
            connBuilder = connBuilder.authKey(authKey);
        }
        conn = connBuilder.connect();
        createTable(TICKERS);
        createTable(TRADES);
    }

    private void createTable(String name) {
        Boolean dbExists = r.dbList().contains(db).run(conn);
        if (!dbExists) {
            r.dbCreate(db).run(conn);
        }

        Boolean exists = r.db(db).tableList().contains(name).run(conn);
        if (!exists) {
            r.db(db).tableCreate(name).run(conn);
        }
    }

    public void insertTicker(TickerDto ticker) {

        MapObject expression = new MapObject();
        for (Field field : TickerDto.class.getDeclaredFields()) {
            try {
                expression.with(field.getName(), field.get(ticker) instanceof Date ? ((Date) field.get(ticker)).getTime() : field.get(ticker));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        r.db(db).table(TICKERS).insert(expression).run(conn);
    }

    public void insertTrade(TradeDto trade) {

        MapObject expression = new MapObject();
        for (Field field : TradeDto.class.getDeclaredFields()) {
            try {
                expression.with(field.getName().equals("id") ? "_id" : field.getName(), field.get(trade) instanceof Date ? ((Date) field.get(trade)).getTime() : field.get(trade));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        r.db(db).table(TRADES).insert(expression).run(conn);
    }
}
