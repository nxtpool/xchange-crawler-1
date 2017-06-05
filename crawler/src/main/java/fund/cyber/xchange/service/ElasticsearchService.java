package fund.cyber.xchange.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fund.cyber.xchange.model.api.OrderDto;
import fund.cyber.xchange.model.api.TradeDto;
import fund.cyber.xchange.model.api.TickerDto;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
public class ElasticsearchService implements InitializingBean {

    @Autowired
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Config config;

    private TransportClient client;

    @Override
    public void afterPropertiesSet() throws Exception {

        String elasticClusterName = config.getProperty("elastic.cluster.name");
        String elasticNodeHost = config.getProperty("elastic.node.host");

        Settings settings = Settings.builder()
                .put("cluster.name", elasticClusterName).build();
        client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(elasticNodeHost), 9300));
    }

    public void insertTicker(TickerDto ticker) throws JsonProcessingException {
        // instance a json mapper
        ObjectMapper mapper = new ObjectMapper(); // create once, reuse

        // generate json
        byte[] json = mapper.writeValueAsBytes(ticker);

        IndexResponse response = client.prepareIndex("market", "ticker")
                .setSource(json)
                .get();
    }

    public void insertTrade(TradeDto trade) throws JsonProcessingException {
        // instance a json mapper
        ObjectMapper mapper = new ObjectMapper(); // create once, reuse

        // generate json
        byte[] json = mapper.writeValueAsBytes(trade);

        IndexResponse response = client.prepareIndex("market", "trade")
                .setSource(json)
                .get();
    }

    public void insertOrder(OrderDto order) throws JsonProcessingException {
        // instance a json mapper
        ObjectMapper mapper = new ObjectMapper(); // create once, reuse

        // generate json
        byte[] json = mapper.writeValueAsBytes(order);

        IndexResponse response = client.prepareIndex("market", "order")
                .setSource(json)
                .get();
    }

}
