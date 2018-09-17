package com.zenika.zthulj.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class SampleConsumerBestPractice {


    static Logger logger = LoggerFactory.getLogger(SampleConsumerBestPractice.class);

    public static void main(String[] args) {

        // These properties should be

        Properties properties = new Properties();

        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9093");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,"MyTest-consumer");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");

        // Latch for multiple threads
        CountDownLatch latch = new CountDownLatch(1);

        Runnable consumerThread = new SampleConsumerRunnable<String,String>(latch, properties,Collections.singleton("testTopic"));
        Thread thread = new Thread(consumerThread);
        thread.start();

        // Shutdown hook to be clean when closing the application ;-)

        Runtime.getRuntime().addShutdownHook(new Thread( ()->{
            ((SampleConsumerRunnable) consumerThread).shutdown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                // Application has been interrupted
                logger.error("Interrupted Exception : ", e);
            }
        }));


        try {
            latch.await();
        } catch (InterruptedException e) {
            // Application has been interrupted
        }finally {
            // Close app
        }
    }
}
