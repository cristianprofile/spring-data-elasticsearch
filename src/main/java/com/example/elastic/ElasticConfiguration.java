package com.example.elastic;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;

import java.net.InetAddress;

@Configuration
public class ElasticConfiguration {

    @Value("${spring.data.elasticsearch.properties.host}")
    private String host;

    @Value("${spring.data.elasticsearch.properties.port}")
    private Integer port;


    @Bean
    public Client client() throws Exception {

        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch-cluster").build();
        TransportClient client = new PreBuiltTransportClient(settings)
                .addTransportAddress(new TransportAddress(InetAddress.getByName(host), port));
        return  client;
    }


    @Bean
    public ElasticsearchTemplate elasticsearchTemplate(Client client,CustomEntityMapper customEntityMapper) {
        return new ElasticsearchTemplate(client, customEntityMapper);
    }


}
