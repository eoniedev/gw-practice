package com.example.gwpractice.reactive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class MonoEx {

    //block
    @GetMapping("/block")
    Mono<String> block() {
        log.info("pos1");
        Mono<String> m = Mono.just("hello world").doOnNext(c -> log.info(c)).log();
        String msg = m.block(); //이후 진행이 막힐 수 있어서 스레드 기아 상태 유발 가능
        log.info("pos2" + msg); //말그대로 blocking

        return m;
    }

    @GetMapping("/")
    Mono<String> hello() {
        log.info("pos1");
        Mono<String> m = Mono.just("hello world").doOnNext(c -> log.info(c)).log();
        log.info("pos2");

        return m;
    }
    @GetMapping("/sup")
    Mono<String> sup() {
        log.info("pos1");
        Mono<String> m = Mono.fromSupplier(()->generateHello()).doOnNext(c -> log.info(c)).log();
        log.info("pos2");

        return m;
    }

    private String generateHello() {
        log.info("method: generateHello");
        return "hello world";
    }


}
