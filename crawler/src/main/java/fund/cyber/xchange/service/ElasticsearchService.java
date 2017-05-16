package fund.cyber.xchange.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fund.cyber.xchange.model.api.TickerDto;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;

@Component
public class ElasticsearchService implements InitializingBean {

    @Value("${elastic.cluster.name}")
    private String elasticClusterName;

    @Value("${elastic.node.host}")
    private String elasticNodeHost;

    private TransportClient client;

    @Override
    public void afterPropertiesSet() throws Exception {
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

}
