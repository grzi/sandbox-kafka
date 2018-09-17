package com.zenika.zthulj.consumer;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class SampleConsumerAssignAndSeek {

    public static void main(String[] args) {
        Logger logger = LoggerFactory.getLogger(SampleConsumerAssignAndSeek.class);
        Properties properties = new Properties();

        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9093");
        //properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,"TestApplication");
        //properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");

        //Create and subscribe to a topic
        Consumer<String,String> consumer = new KafkaConsumer<String, String>(properties);

        TopicPartition partitionToRead = new TopicPartition("testTopic",0);
        consumer.assign(Collections.singleton(partitionToRead));

        //
        consumer.seek(partitionToRead, 5L);


        boolean finishedReading = false;
        int totalToRead = 22;
        int totalRead = 0;


        while(!finishedReading){

            ConsumerRecords<String,String> records = consumer.poll(Duration.ofMillis(100));

            for (ConsumerRecord<String,String> record: records) {

                logger.info("topic : "
                        + record.topic()
                        + " ; partition : "
                        + record.partition()
                        + " ; offset : "
                        + record.offset()
                        + " ; " + record.key()
                        + " ; " + record.value());

                totalRead++;
                if(totalRead>=totalToRead){
                    finishedReading = true;
                    break;
                }
            }

        }
    }
}
