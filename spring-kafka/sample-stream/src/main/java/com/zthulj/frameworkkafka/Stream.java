package com.zthulj.frameworkkafka;

import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
public class Stream {

    public static final String SOURCE = "test-topic-source";
    public static final String TARGET = "test-topic-target";

    @Bean(name = "streamBuilder")
    public Topology getStreamTopology(StreamsBuilder kStreamBuilder){
        kStreamBuilder.stream(SOURCE).mapValues((o, o2) -> o2.toString() + o2.toString()).to(TARGET);
        return kStreamBuilder.build();
    }

}
