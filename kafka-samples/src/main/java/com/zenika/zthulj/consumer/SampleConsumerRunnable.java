package com.zenika.zthulj.consumer;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collection;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class SampleConsumerRunnable<k,v> implements Runnable {

    static Logger logger = LoggerFactory.getLogger(SampleConsumerRunnable.class);


    CountDownLatch latch;
    Consumer<k,v> consumer;

    public SampleConsumerRunnable(CountDownLatch latch, Properties props, Collection<String> topics) {
        this.latch = latch;
        this.consumer = new KafkaConsumer<k, v>(props);
        this.consumer.subscribe(topics);
    }

    @Override
    public void run() {
        try{
            while(true){
                ConsumerRecords<k,v> records = consumer.poll(Duration.ofMillis(100));
                records.forEach(
                        e->logger.info(
                                "topic : " + e.topic()
                                        + " ; partition : "
                                        + e.partition() + " ; "
                                        + e.key()+" ; "
                                        +e.value()));
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
