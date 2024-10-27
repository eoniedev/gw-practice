package com.example.gwpractice.reactive;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Practice1 {
    private static final Logger log  = LoggerFactory.getLogger(Practice1.class);

    public static void main(String[] args) {
        Publisher<Integer> pub = subscriber -> {
            subscriber.onSubscribe(new Subscription() {
                @Override
                public void request(long l) {
                    log.info("reqeust()");
                    subscriber.onNext(1);
                    subscriber.onNext(2);
                    subscriber.onNext(3);
                    subscriber.onNext(4);
                    subscriber.onComplete();
                }

                @Override
                public void cancel() {

                }
            });
        };

        Publisher<Integer> subOnPub = sub -> {
            ExecutorService ex = Executors.newSingleThreadExecutor();
            ex.execute(()->pub.subscribe(sub));
        };

        Publisher<Integer> pubOnPub = sub -> {
            subOnPub.subscribe(new Subscriber<Integer>() {
                ExecutorService ex = Executors.newSingleThreadExecutor();
                @Override
                public void onSubscribe(Subscription s) {
                    sub.onSubscribe(s);
                }

                @Override
                public void onNext(Integer integer) {
                    ex.execute(()-> sub.onNext(integer));
                }

                @Override
                public void onError(Throwable t) {
                    ex.execute(()-> sub.onError(t));
                }

                @Override
                public void onComplete() {
                    ex.execute(()-> sub.onComplete());
                }
            });
        };

        pubOnPub.subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                log.info("onSubscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                log.info("onNext: {}",integer);
            }

            @Override
            public void onError(Throwable throwable) {
                log.info("onError: {}", throwable);
            }

            @Override
            public void onComplete() {
                log.info("onComplete");
            }
        });

        log.info("exit");
    }
}
