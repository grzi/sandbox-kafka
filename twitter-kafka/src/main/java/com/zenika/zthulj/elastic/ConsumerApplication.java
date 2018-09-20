package com.zenika.zthulj.elastic;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ConsumerApplication {
    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(ConsumerApplication.class);

        CountDownLatch latch = new CountDownLatch(1);


        Properties properties = new Properties();

        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "Elastic_consumer");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        Runnable consumer = new ConsumerRunnable<String, String>(latch, properties, Collections.singleton("twitter_sniffer_buffer"));
        Thread thread = new Thread(consumer);
        thread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            ((ConsumerRunnable) consumer).shutdown();
            try {
                latch.await();
                ElasticClient.getInstance().close();
                logger.info("Closed Elastic client and Kafka consumer Threads");
            } catch (InterruptedException e) {
                // ProducerApplication has been interrupted
                logger.error("Interrupted Exception : ", e);
            }
        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("Interrupted : ", e);
        }

        ElasticClient.getInstance().close();
    }
}
