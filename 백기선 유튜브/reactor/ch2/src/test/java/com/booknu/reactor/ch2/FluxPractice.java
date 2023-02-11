package com.booknu.reactor.ch2;

import java.time.Duration;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Flux;

public class FluxPractice {

  @Test
  void emptyFlux() {
    Flux.empty()
      .subscribe(System.out::println);
  }

  @Test
  void fluxFromValues() {
    Flux.just("foo", "bar")
      .subscribe(System.out::println);
  }

  @Test
  void fluxFromList() {
    Flux.fromIterable(List.of("foo", "bar"))
      .subscribe(System.out::println);
  }

  @Test
  void errorFlux() {
    Flux.error(new IllegalStateException())
      .doOnError(err -> System.out.println("----- Error: " + err))
      .subscribe();
  }

  @SneakyThrows
  @Test
  void counter() {
    Flux.interval(Duration.ofMillis(100))
      .take(10)
      .subscribe(System.out::println);

    Thread.sleep(1000);
  }
}
