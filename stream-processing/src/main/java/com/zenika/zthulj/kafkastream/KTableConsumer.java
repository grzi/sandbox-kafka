package com.zenika.zthulj.kafkastream;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.streams.kstream.TimeWindowedDeserializer;
import org.apache.kafka.streams.kstream.Windowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

public class KTableConsumer {
    private static Logger logger = LoggerFactory.getLogger(KTableConsumer.class);

    public static void main(String[] args) {

        Properties properties = new Properties();

        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,"localhost:9093");
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG,"TestAppliscationee4");
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,"earliest");


        final Deserializer<Windowed<String>> windowedDeserializer = new TimeWindowedDeserializer<>(Serdes.String().deserializer());
        final KafkaConsumer<Windowed<String>, Long> consumer = new KafkaConsumer<>(properties,
                windowedDeserializer,
                Serdes.Long().deserializer());

        //Create and subscribe to a topic
        consumer.subscribe(Collections.singleton("twitter_word_counter"));

        while(true){
            ConsumerRecords<Windowed<String>,Long> records = consumer.poll(Duration.ofMillis(100));
            records.forEach(e-> readKtableContent(e));
        }

    }

    private static void readKtableContent(ConsumerRecord<Windowed<String>, Long> e) {
        Long myValue = e.value();
        logger.info("topic : " + e.topic() + " ; partition : " + e.partition() + " ; " + e.key().key() + ":" + e.key().window().start() + ":" + e.key().window().end() +" ; "+myValue);
    }
}
