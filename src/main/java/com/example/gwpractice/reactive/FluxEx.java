package com.example.gwpractice.reactive;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class FluxEx {
	@GetMapping("/event/{id}")
	Mono<List<Event>> hello(@PathVariable long id) {
		return Mono.just(Arrays.asList(new Event (1l, "event1"), new Event(2l, "event2")));
	}

	@GetMapping("/event2")
	Flux<Event> event2() {
		List<Event> list = Arrays.asList(new Event (1l, "event1"), new Event(2l, "event2"));
		return Flux.fromIterable(list);
	}

	//스트림으로 보내는 옵션
	@GetMapping(value = "/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<Event> events() {
		return Flux.just(new Event (1l, "event1"), new Event(2l, "event2"));
	}


	@GetMapping(value = "/events2", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<Event> events2() {
		Stream<Event> stream = Stream.generate(() -> new Event(System.currentTimeMillis(), "value"));
		return Flux.fromStream(stream)
				.delayElements(Duration.ofMillis(1000))		// background 스레드를 통해 10초 동안 처리됨
				.take(10);
	}


	@GetMapping(value = "/events3", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<Event> events3() {
		return Flux.<Event>generate(sink -> sink.next(new Event(System.currentTimeMillis(), "value")))		//consumer를 받는 제너레이터, callable받는 제너레이터
				.delayElements(Duration.ofMillis(1000))		// background 스레드를 통해 10초 동안 처리됨
				.take(10);
	}

	@GetMapping(value = "/events4", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<Event> events4() {
		return Flux.<Event, Long>generate(() -> 1L, (id, sink) -> {
					sink.next(new Event(id, "value" + id));
					return id+1;
				})        //callable(초기값), bifunc받는 제너레이터
				.delayElements(Duration.ofMillis(1000))        // background 스레드를 통해 10초 동안 처리됨
				.take(10);
	}

	@GetMapping(value = "/events5", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<Event> events5() {
		Flux<Event> es = Flux.<Event, Long>generate(() -> 1L, (id, sink) -> {
			sink.next(new Event(id, "value" + id));
			return id + 1;
		});

		Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));

		//쌍대로 zipping하기 때문에 후자가 1초에 한번만 데이터를 만드니까 자연스럽게 딜레이 거는 효과가 있음.
		return Flux.zip(es, interval)
				.map(tu -> tu.getT1())
				.take(10);
	}

	@GetMapping(value = "/events6", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<Event> events6() {
		Flux<String> es = Flux.generate(sink -> sink.next("value"));
		Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));

		//이렇게 해서 활용도 가능
		return Flux.zip(es, interval)
				.map(tu-> new Event(tu.getT2(), tu.getT1()))
				.take(10);
	}


	@Data
	@AllArgsConstructor
	public static class Event {
		long id;
		String value;
	}

}
