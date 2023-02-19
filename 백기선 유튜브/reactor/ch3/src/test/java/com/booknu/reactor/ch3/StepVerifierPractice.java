package com.booknu.reactor.ch3;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class StepVerifierPractice {

  @Test
  void expectFooBarComplete() {
    var flux = Flux.just("foo", "bar");

    StepVerifier.create(flux)
      .expectNext("foo")
      .expectNext("bar")
      .verifyComplete();
  }

  @Test
  void expectFooBarError() {
    var flux = Flux.concat(
      Flux.just("foo", "bar"),
      Flux.error(new RuntimeException()));

    StepVerifier.create(flux)
      .expectNext("foo")
      .expectNext("bar")
      .verifyError(RuntimeException.class);
  }

  @Getter
  @Accessors(fluent = true)
  @RequiredArgsConstructor(staticName = "name")
  public static class User {
    private final String name;
  }

  @Test
  void expectObject() {
    var flux = Flux.just(
      User.name("x"),
      User.name("y")
    );

    StepVerifier.create(flux)
      .assertNext(user -> assertEquals("x", user.name()))
      .assertNext(user -> assertEquals("y", user.name()))
      .verifyComplete();
  }

  @Test
  void expect10Elements() {
    var flux = Flux.interval(Duration.ofMillis(500))
      .take(10);

    StepVerifier.create(flux)
      .expectNextCount(10)
      .verifyComplete();
  }

  @Test
  void expectLongDelayMono() {
    // 딜레이가 매우 긴 Mono
    // supplier 내부에서 publisher 를 생성해야 가상 시간이 적용됨
    StepVerifier.withVirtualTime(() -> Mono.delay(Duration.ofHours(3)))
      .expectSubscription() // expectNoEvent 를 사용하기 바로 전 반드시 expectSubscription 사용
      .expectNoEvent(Duration.ofHours(2))
      .thenAwait(Duration.ofHours(2))
      .expectNextCount(1)
      .verifyComplete();
  }

  @Test
  void expect3600Elements() {
    // 처리에 매우 오래 걸리는 flux
    Supplier<Flux<Long>> fluxSupplier = () ->
      Flux.interval(Duration.ofSeconds(1))
        .take(3600)
        .map(x -> x * 2);

    StepVerifier.withVirtualTime(fluxSupplier)
      .thenAwait(Duration.ofSeconds(3600))
      .expectNextCount(3600)
      .expectComplete()
      .log()
      .verify();
  }

  @Test
  void expect3600Elements2() {
    // 처리에 매우 오래 걸리는 flux
    var flux = Flux.interval(Duration.ofSeconds(1))
        .take(3600);

    Duration execTime = StepVerifier.withVirtualTime(() -> flux, 3600)
      .thenAwait(Duration.ofSeconds(3600))
      .expectNextCount(3600)
      .expectComplete()
      .log()
      .verify();

    System.out.println(execTime);
  }
}
