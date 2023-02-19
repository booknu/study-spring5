package com.booknu.reactor.ch7;

import java.util.function.Function;
import org.junit.jupiter.api.*;
import reactor.core.Exceptions;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class HandlingErrorPractice {

  @Test
  @DisplayName("onErrorReturn: 에러 발생 시 기본값으로 변환")
  void onErrorReturn() {
    Mono<String> successMono = Mono.just("success");
    Mono<String> errorMono = Mono.error(IllegalStateException::new);

    Function<Mono<String>, Mono<String>> errorHandledMonoSupplier = mono -> mono
      .log()
      .onErrorReturn(IllegalStateException.class, "default")
      .log();

    StepVerifier.create(errorHandledMonoSupplier.apply(successMono))
      .expectNext("success")
      .verifyComplete();

    StepVerifier.create(errorHandledMonoSupplier.apply(errorMono))
      .expectNext("default")
      .verifyComplete();
  }

  @Test
  @DisplayName("onErrorResume: 에러 발생 시 단일 값이 아닌 기본 시퀀스를 반환")
  void onErrorResume() {
    Flux<String> flux = Flux.merge(Flux.just("1", "2", "3").log(), Flux.error(IllegalStateException::new))
      .log()
      .onErrorResume(IllegalStateException.class, error -> Flux.just("default1", "default2"))
      .log();

    StepVerifier.create(flux)
      .expectNext("1", "2", "3", "default1", "default2")
      .verifyComplete();
  }

  private static class SomeCheckedException extends Exception {

  }

  @Test
  @DisplayName("Exceptions_propagate: Exceptions.propagate 를 사용하면 CheckedException 을 RuntimeException 으로 감싸줌. "
    + "--> StepVerifier 에서는 이걸 unwrap 해서 볼 수 있음")
  void Exceptions_propagate() {
    Flux<String> flux = Flux.just("1", "2", "3", "error")
      .log()
      .map(item -> {
        try {
          if ("error".equals(item)) {
            throw new SomeCheckedException();
          }

          return item;
        } catch (SomeCheckedException e) {
          throw Exceptions.propagate(e);
        }
      })
      .log()
      .onErrorResume(SomeCheckedException.class, error -> Flux.just("default1", "default2"))
      .log();

    StepVerifier.create(flux)
      .expectNext("1", "2", "3", "default1", "default2")
      .verifyComplete();
  }
}
