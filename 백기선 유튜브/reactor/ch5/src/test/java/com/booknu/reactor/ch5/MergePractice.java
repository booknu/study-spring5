package com.booknu.reactor.ch5;

import java.time.Duration;
import java.util.function.Supplier;
import java.util.stream.IntStream;
import org.junit.jupiter.api.*;
import org.springframework.boot.convert.DurationFormat;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

public class MergePractice {

  @Test
  @DisplayName("가장 간단한 merge: 단순하게 두 flux 를 합침")
  void merge() {

    VirtualTimeScheduler virtualTimeScheduler = VirtualTimeScheduler.create();

    Supplier<Flux<String>> flux1 = () -> Flux.range(0, 4)
      .delayElements(Duration.ofMillis(200), virtualTimeScheduler)
      .map(x -> "flux1: " + x)
      .doOnNext(System.out::println);

    Supplier<Flux<String>> flux2 = () -> Flux.range(0, 4)
      .map(x -> "flux2: " + x)
      .doOnNext(System.out::println);

    // flux.mergeWith() 메소드를 사용해도 됨.
    StepVerifier.withVirtualTime(() -> Flux.merge(flux1.get(), flux2.get()))
      .expectSubscription()
      .then(() -> virtualTimeScheduler.advanceTimeBy(Duration.ofMillis(200_000)))
      .expectNextSequence(IntStream.range(0, 4).mapToObj(x -> "flux2: " + x).toList())
      .expectNextSequence(IntStream.range(0, 4).mapToObj(x -> "flux1: " + x).toList())
      .verifyComplete();
  }

  @Test
  @DisplayName("순서를 지키면서 두 flux 를 합침")
  void merge_keepOrder() {
    VirtualTimeScheduler virtualTimeScheduler = VirtualTimeScheduler.create();

    Supplier<Flux<String>> flux1 = () -> Flux.range(0, 4)
      .delayElements(Duration.ofMillis(200), virtualTimeScheduler)
      .map(x -> "flux1: " + x)
      .doOnNext(System.out::println);

    Supplier<Flux<String>> flux2 = () -> Flux.range(0, 4)
      .map(x -> "flux2: " + x)
      .doOnNext(System.out::println);

    // flux.concat() 혹은 Flux.concat() 사용 가능 --> 이 경우 flux1 이벤트 실행 완료 후 flux2 이벤트가 실행됨.
    // Flux.mergeSequential() 의 경우 flux1, flux2 이벤트가 동시에 실행됨.
    StepVerifier.withVirtualTime(() -> Flux.mergeSequential(flux1.get(), flux2.get()))
      .expectSubscription()
      .then(() -> virtualTimeScheduler.advanceTimeBy(Duration.ofMillis(200_000)))
      .expectNextSequence(IntStream.range(0, 4).mapToObj(x -> "flux1: " + x).toList())
      .expectNextSequence(IntStream.range(0, 4).mapToObj(x -> "flux2: " + x).toList())
      .verifyComplete();

  }

  @Test
  @DisplayName("Publisher 인터페이스는 모두 합칠 수 있음")
  void merge_mono() {
    var mono1 = Mono.just("mono1")
      .doOnNext(System.out::println);

    var flux1 = Flux.range(1, 4)
      .map(x -> "flux" + x)
      .doOnNext(System.out::println);

    StepVerifier.withVirtualTime(() -> Flux.merge(mono1, flux1))
      .expectSubscription()
      .expectNext("mono1")
      .expectNextSequence(IntStream.range(1, 5).mapToObj(x -> "flux" + x).toList())
      .verifyComplete();

  }
}
