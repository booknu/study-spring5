package com.booknu.reactor.ch6;

import java.time.Duration;
import java.util.function.Supplier;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.logging.LogLevel;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

@Slf4j
public class BackpressureExample {

  @Test
  @DisplayName("버퍼 느낌으로 일부 데이터만 request 하는 방법")
  void basic() {
    Flux.range(0, 20)
      .log()
      .subscribe(new Subscriber<>() {

        private Subscription subscription;
        private int remainCount;

        // 최초로 실행되는 메소드
        @Override
        public void onSubscribe(Subscription s) {
          log.info("Subscribed!");
          subscription = s;
          requestIfEmpty(10);
        }

        @Override
        public void onNext(Integer integer) {
          itemConsumed();
          requestIfEmpty(5);
          log.info("next: {}", integer);
        }

        @Override
        public void onError(Throwable t) {
          log.error("error! {}", t.getMessage());
          t.printStackTrace();
        }

        // 마지막에 실행되는 메소드
        @Override
        public void onComplete() {
          log.info("Completed!");
        }

        private void itemConsumed() {
          --remainCount;
        }

        private void requestIfEmpty(int size) {
          if (remainCount == 0) {
            subscription.request(size);
            log.info("request: {}", size);
            remainCount = size;
          }
        }
      });
  }

  @Test
  @DisplayName("Spring webflux 에서는 backpressure 을 알아서 조절해주는듯함.")
  void simple() {
    Flux.range(0, 100)
      .log()
      .onBackpressureBuffer(10) // ??
      .log()
      .doOnNext(System.out::println)
      .log()
      .doOnRequest(x -> log.info("request: {}", x))
      .blockLast();
  }

  @Test
  @DisplayName("이건 왜 에러 발생?")
  void test() {

    Flux.range(0, 100)
      .log()
      .onBackpressureBuffer(10)
      .delayElements(Duration.ofMillis(100))
      .log()
      .doOnNext(System.out::println)
      .log()
      .doOnRequest(x -> log.info("request: {}", x))
      .blockLast();
  }

  @Test
  @DisplayName("delay 를 onBackpressureBuffer 앞에 놓으면 괜찮음")
  void test2() {

    Flux.range(0, 100)
      .log()
      .delayElements(Duration.ofMillis(100))
      .onBackpressureBuffer(10)
      .log()
      .doOnNext(System.out::println)
      .log()
      .doOnRequest(x -> log.info("request: {}", x))
      .blockLast();
  }
}
