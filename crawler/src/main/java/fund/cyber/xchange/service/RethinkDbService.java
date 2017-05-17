package fund.cyber.xchange.service;

import com.rethinkdb.RethinkDB;
import com.rethinkdb.model.MapObject;
import com.rethinkdb.net.Connection;
import fund.cyber.xchange.model.api.TickerDto;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Properties;

@Component
public class RethinkDbService implements InitializingBean {

    private static final String TICKERS = "tickers";
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
        createTickerTable();
    }

    private void createTickerTable() {
        Boolean dbExists = r.dbList().contains(db).run(conn);
        if (!dbExists) {
            r.dbCreate(db).run(conn);
        }

        Boolean exists = r.db(db).tableList().contains(TICKERS).run(conn);
        if (!exists) {
            r.db(db).tableCreate(TICKERS).run(conn);
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

}
