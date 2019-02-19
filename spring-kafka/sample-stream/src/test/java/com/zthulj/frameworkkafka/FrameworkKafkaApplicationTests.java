package com.zthulj.frameworkkafka;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyTestDriver;
import org.apache.kafka.streams.test.ConsumerRecordFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Properties;

public class FrameworkKafkaApplicationTests {


    private Topology streamTopology;
    private TopologyTestDriver testDriver;
    private StringDeserializer stringDeserializer = new StringDeserializer();
    private ConsumerRecordFactory<String, String> recordFactory = new ConsumerRecordFactory<>(new StringSerializer(), new StringSerializer());


    @Before
    public void prepare(){

        Stream myStream = new Stream();
        StreamsBuilder builder = new StreamsBuilder();
        this.streamTopology = myStream.getStreamTopology(builder);

        Properties config = new Properties();
        config.setProperty(StreamsConfig.APPLICATION_ID_CONFIG, "maxAggregation");
        config.setProperty(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "dummy:1234");
        config.setProperty(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        config.setProperty(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        testDriver = new TopologyTestDriver(this.streamTopology, config);


    }

    @Test
    public void test(){
        testDriver.pipeInput(recordFactory.create(Stream.SOURCE, "key", "message"));
        Assert.assertEquals("messagemessage",testDriver.readOutput(Stream.TARGET, stringDeserializer, stringDeserializer).value());
    }

}

