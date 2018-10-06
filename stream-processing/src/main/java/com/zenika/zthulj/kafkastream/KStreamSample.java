package com.zenika.zthulj.kafkastream;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;

import java.util.Arrays;
import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class KStreamSample {
    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-wordcount2");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9093");
        props.put(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());

        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        StreamsBuilder streamBuilder = new StreamsBuilder();

        KStream<String, String> kStream = streamBuilder.stream("twitter_sniffer_buffer");

        // Pour me permettre de récupérer les occurences de 'one' et de 'two' dans les tweet qui passent dans kafka
        KTable<Windowed<String>, Long> kTable =
        kStream.flatMapValues(value -> Arrays.asList(extractTweet(value)))
                .flatMapValues(value -> Arrays.asList(value.toLowerCase(Locale.getDefault()).split(" ")))
                .filter((key, value) -> isSearched(value))
                .groupBy((key, value) -> value)
                .windowedBy(TimeWindows.of(1000))
                .count();

        Serde<Windowed<String>> windowedSerde = WindowedSerdes.timeWindowedSerdeFrom(String.class);

        kTable.toStream().to("twitter_word_counter", Produced.with(windowedSerde, Serdes.Long()));
        final KafkaStreams kafkaStream = new KafkaStreams(streamBuilder.build(),props);
        final CountDownLatch latch = new CountDownLatch(1);

        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-wordcount-shutdown-hook") {
            @Override
            public void run() {
                kafkaStream.close();
                latch.countDown();
            }
        });

        try {
            kafkaStream.start();
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);


    }

    private static boolean isSearched(String value) {
        return "one".equals(value) || "two".equals(value);
    }

    public static String extractTweet(String message) {
        JsonObject jsonObject = new JsonParser().parse(message).getAsJsonObject();
        JsonElement obj = jsonObject.get("text");
        return obj!=null ? obj.toString() : "";
    }
}
