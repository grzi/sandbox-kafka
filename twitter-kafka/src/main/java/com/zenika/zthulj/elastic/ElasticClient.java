package com.zenika.zthulj.elastic;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.http.HttpHost;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.Index;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

// TODO : Make this as a singleton
public class ElasticClient {

    static Logger logger = LoggerFactory.getLogger(ElasticClient.class);
    private RestHighLevelClient client;
    private static Gson gson = new Gson();

    private ElasticClient() {
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));
    }



    public void indexRecords(ConsumerRecords<?,?> records){
        if(records.count() > 0){


        BulkRequest bulk = new BulkRequest();
        records.forEach(e->bulk.add(createIndexRequestFromRecord(e)));
            try {
                BulkResponse response = client.bulk(bulk,RequestOptions.DEFAULT);
                logger.info("Indexed in Elastic" );
                // Here I should be handling errors from the response
            } catch (IOException e) {
               logger.error("Error while indexing into elastic search", e);
            }


        }
    }

    /**
     * This method will create a request and use the client to send records to ES
     * @param record a record from kafka
     */
    private IndexRequest createIndexRequestFromRecord(ConsumerRecord<?,?> record) {

        //Just logging some header information$
        // record.headers().forEach(e -> logger.info(" key : " + e.key() + " ; " + e.value()));

        // Creation of the request
        IndexRequest request = new IndexRequest(
                "posts",
                "doc",
                getTweetIdFromRecord(record));

        request.source(record.value().toString(), XContentType.JSON);

        return request;
    }

    private String getTweetIdFromRecord(ConsumerRecord<?, ?> record) {
        if(null != record.value()){
            JsonObject jsonObject = gson.fromJson(record.value().toString(),JsonObject.class);
            JsonElement id = jsonObject.get("id_str");
            if(id != null ){
                return id.getAsString();
            }
        }
        logger.error("No id found in record : " + record);
        return null;

    }

    public void close() {
        try {
            client.close();
        } catch (IOException e) {
           logger.error("Error while closing client ", e);
        }
    }

    /* Singleton handling */

    private static class SingletonHolder{
        private static final ElasticClient instance = new ElasticClient();
    }

    public static ElasticClient getInstance(){
        return SingletonHolder.instance;
    }
}
