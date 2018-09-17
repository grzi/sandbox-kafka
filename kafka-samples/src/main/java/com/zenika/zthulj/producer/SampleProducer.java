package com.zenika.zthulj.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

public class SampleProducer {
    public static void main (String args[]){

        // Just a sample producer that will send one message to kafka

        Properties properties = new Properties();
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.put(ProducerConfig.ACKS_CONFIG, "all");
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9093");
        properties.put(ProducerConfig.BUFFER_MEMORY_CONFIG, "33554432");
        properties.put(ProducerConfig.COMPRESSION_TYPE_CONFIG, "none");
        properties.put(ProducerConfig.RETRIES_CONFIG,0);
        properties.put(ProducerConfig.CLIENT_ID_CONFIG ,"my-firstkafka-producer");

        Producer<String,String> producer = new KafkaProducer<String,String>(properties);
        producer.send(new ProducerRecord<>("setProperty", "jaune","My message is a cool one"));

        producer.close();

    }
}
