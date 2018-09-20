package com.zenika.zthulj.elastic;

import com.sun.org.apache.xpath.internal.operations.String;
import org.apache.kafka.clients.consumer.ConsumerInterceptor;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class ConsumerInterceptorImpl implements ConsumerInterceptor<String, String> {

    Logger logger = LoggerFactory.getLogger(ConsumerInterceptor.class);
    @Override
    public ConsumerRecords<String, String> onConsume(ConsumerRecords<String, String> records) {

        return records;
    }

    @Override
    public void onCommit(Map<TopicPartition, OffsetAndMetadata> offsets) {

    }

    @Override
    public void close() {

    }

    @Override
    public void configure(Map<java.lang.String, ?> configs) {

    }
}
