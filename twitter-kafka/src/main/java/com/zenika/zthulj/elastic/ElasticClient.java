package com.zenika.zthulj.elastic;

import org.apache.http.HttpHost;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class ElasticClient {
    static Logger logger = LoggerFactory.getLogger(ElasticClient.class);
    public static void main(String[] args) throws IOException {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http")));

        IndexRequest request = new IndexRequest(
                "posts",
                "doc",
                "1");
        String jsonString = "{" +
                "\"user\":\"kimchy\"," +
                "\"postDate\":\"2013-01-30\"," +
                "\"message\":\"trying out Elasticsearch\"" +
                "}";
        request.source(jsonString, XContentType.JSON);

        try {
            client.index(request,RequestOptions.DEFAULT);
        } catch (IOException e) {
            logger.error("Error while calling elastic : ", e);
        }

        GetRequest getReq = new GetRequest("posts","doc","1");

            GetResponse resp = client.get(getReq,RequestOptions.DEFAULT);
            logger.info("result : " + resp.getSourceAsString());



            client.close();
    }
}
