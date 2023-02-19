package com.booknu.reactor.ch6;

import java.time.Duration;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;


/**
 * StepVerifier 테스트 위주
 */
@Slf4j
public class RequestPractice {

  @Test
  void requestAllExpectFour() {
    var flux = Flux.range(0, 4)
      .log() // 로그 확인
      .doOnRequest(x -> log.info("request: {}", x)) // do[On]... 메소드는 데이터 peek 용도 (사이드이펙트 없음) -> latency 가 있는 로직은 여기서 호출하면 안 됨.
      .doOnNext(x -> log.info("next: {}", x))
      .doOnEach(signal -> log.info("signal: {}", signal))
      .doOnSubscribe(subscription -> log.info("subscription: {}", subscription));

    StepVerifier.create(flux)
      .expectNextCount(4)
      .verifyComplete();
  }

  @Test
  @DisplayName("thenRequest, thenCancel: 더 이상 request 가 없는 경우 source 는 끝나지 않음. "
    + "cancel 을 통해 더 이상 request 가 없음을 알림")
  void thenRequestAndThenCancel() {
    VirtualTimeScheduler virtualTimeScheduler = VirtualTimeScheduler.create();

    Supplier<Flux<Integer>> fluxSupplier = () -> Flux.interval(Duration.ofMillis(1000),
        virtualTimeScheduler)
      .log()
      .map(x -> 1)
      .log() // log 는 각 publisher 마다 붙여줘야 하나? do[On]... 도?
      .doOnRequest(x -> log.info("request: {}", x))
      .doOnNext(x -> log.info("next: {}", x))
      .doOnEach(signal -> log.info("signal: {}", signal))
      .doOnSubscribe(subscription -> log.info("subscription: {}", subscription));

    StepVerifier.withVirtualTime(fluxSupplier)
      .thenRequest(1) // 이 코드를 빼고 실행하면 로그에 request(1) 이 남지 않음.
      .then(() -> virtualTimeScheduler.advanceTimeBy(Duration.ofMillis(10_000)))
      .expectNext(1)
      .thenCancel() // cancel 을 넣어주면 expect{} 없이 바로 검증을 끝낼 수 있음
      .verify();
  }

  @Test
  void thenRequestAndThenCancel_1() {
    var flux = Flux.range(0, 10)
      .log()
      .doOnRequest(x -> log.info("request: {}", x))
      .doOnNext(x -> log.info("next: {}", x))
      .doOnEach(signal -> log.info("signal: {}", signal))
      .doOnSubscribe(subscription -> log.info("subscription: {}", subscription));

    StepVerifier.create(flux)
      .thenRequest(1) // 이 코드를 빼고 실행하면 로그에 request(1) 이 남지 않음. (unbounded 로 호출하는듯)
      .expectNext(0)
      .thenCancel()
      .verify();
  }

  @Test
  @DisplayName("expectTimeout: 시간을 기다려도 더 이상 request 가 없음을 검증")
  void expectTimeout() {
    VirtualTimeScheduler virtualTimeScheduler = VirtualTimeScheduler.create();

    Supplier<Flux<Integer>> fluxSupplier = () -> Flux.interval(Duration.ofMillis(1000),
        virtualTimeScheduler)
      .log()
      .map(x -> 1)
      .log()
      .doOnRequest(x -> log.info("request: {}", x))
      .doOnNext(x -> log.info("next: {}", x))
      .doOnEach(signal -> log.info("signal: {}", signal))
      .doOnSubscribe(subscription -> log.info("subscription: {}", subscription));

    StepVerifier.withVirtualTime(fluxSupplier)
      .thenRequest(1)
      .then(() -> virtualTimeScheduler.advanceTimeBy(Duration.ofMillis(1000)))
      .expectNext(1)
      .expectTimeout(Duration.ofSeconds(10))
      .verify();
  }


}
