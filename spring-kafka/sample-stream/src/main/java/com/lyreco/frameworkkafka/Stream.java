package com.lyreco.frameworkkafka;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Stream {

    @Bean
    public KStream<Integer, String> ultraComplexStream(StreamsBuilder kStreamBuilder) {
        KStream<Integer, String> stream = kStreamBuilder.stream("topicInput");
        stream = stream.mapValues(e -> e.toUpperCase());
        stream.to("topicResult");
        return stream;
    }

}
