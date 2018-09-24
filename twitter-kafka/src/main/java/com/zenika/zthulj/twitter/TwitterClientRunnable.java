package com.zenika.zthulj.twitter;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class TwitterClientRunnable implements Runnable{

    static Logger logger = LoggerFactory.getLogger(TwitterClientRunnable.class);

    private CountDownLatch latch;
    private BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(10000);
    private BlockingQueue<Event> eventQueue = new LinkedBlockingQueue<Event>(1000);
    private Client hosebirdClient;
    private KafkaProducerClient kafkaProducer;

    public TwitterClientRunnable(CountDownLatch l) {

        this.latch = l;
        this.kafkaProducer = new KafkaProducerClient();

        Hosts hosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesFilterEndpoint hosebirdEndpoint = new StatusesFilterEndpoint();

        hosebirdEndpoint.trackTerms(Lists.newArrayList("the"));

        Authentication hosebirdAuth = new OAuth1(
                "iY9nECTE4Hg8ZDeeZs0BbF5N2",
                "GxNKVmsKzZ1g2kXFLtvzjzgeKPmK3h3DFhUeT8xs3KCuzj1QHb",
                "476412032-n1Lh6U3vXMwGU2IJQLphiscLLZRhGleMDt3t35aT",
                "oTT3kNLEDmf2X4NtiCllkz6BfgbEqEaZv4M8hF6AoGoTZ"
        );

        ClientBuilder builder = new ClientBuilder()
                .name("Test-sniffer-kafka-tweets")
                .hosts(hosts)
                .authentication(hosebirdAuth)
                .endpoint(hosebirdEndpoint)
                .processor(new StringDelimitedProcessor(msgQueue))
                .eventMessageQueue(eventQueue);

        hosebirdClient = builder.build();
    }

    @Override
    public void run() {
        hosebirdClient.connect();

        while (!hosebirdClient.isDone()) {
            try {
                String msg = msgQueue.poll(5, TimeUnit.SECONDS);
                if(null != msg){
                    kafkaProducer.sendTweet(msg);
                }
            } catch (InterruptedException e) {
                logger.error("Msg Queue has been interrupted : " , e);
                shutdown();
            }
        }


    }

    public void shutdown(){
        hosebirdClient.stop();
        kafkaProducer.close();
        latch.countDown();
    }
}
