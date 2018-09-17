package com.zenika.zthulj.producer;

import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class SampleProducerWithCallback {
    public static void main (String args[]){

        Logger logger = LoggerFactory.getLogger(SampleProducerWithCallback.class);

        // Just a sample producer that will send one message to kafka

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9093");
        properties.setProperty(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");
        properties.setProperty(ProducerConfig.COMPRESSION_TYPE_CONFIG, "none");
        properties.setProperty(ProducerConfig.RETRIES_CONFIG,"0");
        properties.setProperty(ProducerConfig.CLIENT_ID_CONFIG ,"my-firstkafka-producer");

        Producer<String,String> producer = new KafkaProducer<String,String>(properties);
        for(int i = 1 ; i<50;i++) {
            producer.send(new ProducerRecord<>("testTopic", "jaune", "My message" + i +"  with callback working XD "), new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (null == exception) {
                        logger.info("Success : { topic : " + metadata.topic()
                                + ", partition : " + metadata.partition()
                                + ", offset : " + metadata.offset() + " }");
                    } else {
                        logger.error("Error : ", exception);
                    }
                }
            });

        }

        producer.close();

    }
}
