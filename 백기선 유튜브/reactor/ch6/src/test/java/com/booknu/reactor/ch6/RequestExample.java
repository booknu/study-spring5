package com.booknu.reactor.ch6;

import java.time.Duration;
import java.util.stream.IntStream;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Flux;

public class RequestExample {

  @Test
  @DisplayName("항상 request 기본 값이 unbounded 는 아닌 것 같음.")
  void request_log() {
    Flux.range(0, Integer.MAX_VALUE)
      .log()
      .map(x -> "s" + x)
      .log()
      .delayElements(Duration.ofMillis(1000))
      .log()
      .doOnNext(System.out::println)
      .log()
      .blockLast();
  }

  @Test
  void request_log_list() {
    var list = IntStream.range(0, 1_000_000).boxed().toList();

    Flux.fromIterable(list)
      .log()
      .map(x -> "s" + x)
      .log()
      .delayElements(Duration.ofMillis(1000))
      .log()
      .doOnNext(System.out::println)
      .log()
      .blockLast();
  }
}
