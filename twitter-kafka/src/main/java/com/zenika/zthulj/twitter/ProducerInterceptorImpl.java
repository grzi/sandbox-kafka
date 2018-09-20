package com.zenika.zthulj.twitter;

import org.apache.kafka.clients.producer.ProducerInterceptor;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ProducerInterceptorImpl implements ProducerInterceptor<String,String>{

    Logger logger = LoggerFactory.getLogger(ProducerInterceptorImpl.class);

    @Override
    public ProducerRecord<String, String> onSend(ProducerRecord<String, String> record) {
        String header = "Some metric value"  + record.key();
        record.headers().add("Metric1", header.getBytes());
        return record;
    }

    @Override
    public void onAcknowledgement(RecordMetadata metadata, Exception exception) {

    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<String, ?> configs) {

    }
}
