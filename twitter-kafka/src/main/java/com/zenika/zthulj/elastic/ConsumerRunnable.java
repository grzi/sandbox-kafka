package com.zenika.zthulj.elastic;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.elasticsearch.action.bulk.BulkRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class ConsumerRunnable<k,v> implements Runnable {

    static Logger logger = LoggerFactory.getLogger(ConsumerRunnable.class);

    CountDownLatch latch;
    Consumer<k,v> consumer;

    public ConsumerRunnable(CountDownLatch latch, Properties props, Collection<String> topics) {
        this.latch = latch;
        this.consumer = new KafkaConsumer<k, v>(props);
        this.consumer.subscribe(topics);
    }

    @Override
    public void run() {
        try{

            while(true){
                ConsumerRecords<k,v> records = consumer.poll(Duration.ofMillis(100));
                logger.info("received " + records.count() + " records");
                ElasticClient.getInstance().indexRecords(records);
                logger.info("Committed the offsets");
                consumer.commitSync();
            }
        }catch(WakeupException e){

        }finally{
            consumer.close();
            latch.countDown();
        }
    }

    public void shutdown(){
        consumer.wakeup();
    }
}
