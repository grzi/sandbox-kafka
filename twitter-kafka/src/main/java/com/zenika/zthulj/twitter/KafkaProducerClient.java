package com.zenika.zthulj.twitter;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.record.CompressionType;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class KafkaProducerClient {

    static Logger logger = LoggerFactory.getLogger(KafkaProducerClient.class);


    Producer<String,String> kafkaProducer;
    private String topic = "twitter_sniffer_buffer";


    public KafkaProducerClient() {
        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9093,localhost:9094");
        properties.setProperty(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");

        properties.setProperty(ProducerConfig.RETRIES_CONFIG,Integer.toString(Integer.MAX_VALUE));
        properties.setProperty(ProducerConfig.CLIENT_ID_CONFIG ,"tweeter-reader");
        properties.setProperty(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG,"true");
        properties.setProperty(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, "5");

        properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "snappy");
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "20");
        properties.setProperty(ProducerConfig.BATCH_SIZE_CONFIG, Integer.toString(32*1024));

        properties.setProperty(ProducerConfig.INTERCEPTOR_CLASSES_CONFIG,"com.zenika.zthulj.twitter.ProducerInterceptorImpl");


        kafkaProducer = new KafkaProducer<String,String>(properties);

    }

    public void sendTweet(String tweet){
        kafkaProducer.send(
                new ProducerRecord<>(topic, tweet),
                (RecordMetadata metadata, Exception exception) -> {
            if(exception == null)
                    logger.info("Success : { topic : " + metadata.topic()
                        + ", partition : " + metadata.partition()
                        + ", offset : " + metadata.offset() + " , tweet : " + tweet + "}");
                else
                    logger.error("error while sending to kafka : ", exception);

        });
    }

    public void close(){
        kafkaProducer.close();
    }
}
