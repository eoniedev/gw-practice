package com.example.gwpractice.reactive;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

@Slf4j
@RestController
public class FluxEx2 {
    @GetMapping("/rest")
    Mono<String> restTest() {
        Mono<String> res = WebClient.create()
                .method(HttpMethod.GET)
                .uri(URI.create("/test"))
                .header("Brand", "K")
                .retrieve()
                .bodyToMono(String.class);
        return res;
    }

    @GetMapping("/rest2")
    Mono<ResponseEntity<String>> restTest2() {
        Mono<ResponseEntity<String>> entity = WebClient.create()
                .method(HttpMethod.GET)
                .uri(URI.create("/test"))
                .header("Brand", "K")
                .retrieve()
                .toEntity(String.class);
        return entity;
    }

    @GetMapping("/rest3")
    Mono<String> restTest3() {
        Mono<String> entity = WebClient.create()
                .method(HttpMethod.GET)
                .uri(URI.create("/test"))
                .header("Brand", "K")
                .exchange()                 // exchange deprecated -> memory leak 위험
                .flatMap(res -> {
                    return res.bodyToMono(String.class);
                });
        return entity;
    }
}
