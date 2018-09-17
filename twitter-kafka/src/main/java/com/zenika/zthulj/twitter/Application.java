package com.zenika.zthulj.twitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;

public class Application {
    static Logger logger = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {


        CountDownLatch latch = new CountDownLatch(1);

        Runnable twitterClient = new TwitterClientRunnable(latch);
        Thread thread = new Thread(twitterClient);
        thread.start();

        Runtime.getRuntime().addShutdownHook(new Thread( ()->{
            ((TwitterClientRunnable) twitterClient).shutdown();
            try {
                latch.await();
            } catch (InterruptedException e) {
                // Application has been interrupted
                logger.error("Interrupted Exception : ", e);
            }
        }));

        try {
            latch.await();
        } catch (InterruptedException e) {
            logger.error("Interrupted Exception : ", e);
        }finally{
            logger.info("Application closed");
        }
    }
}
