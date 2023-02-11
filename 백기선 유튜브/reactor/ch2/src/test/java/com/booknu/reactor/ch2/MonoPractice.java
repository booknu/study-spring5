package com.booknu.reactor.ch2;

import org.junit.jupiter.api.*;
import reactor.core.publisher.Mono;

public class MonoPractice {

  @Test
  void empty() {
    Mono.empty()
      .doOnSuccess(x -> System.out.println("SUCCESS!"))
      .subscribe(System.out::println);
  }

  /**
   * 아무런 신호도 모내지 않는 Mono (complete 신호도 없음)
   */
  @Test
  void never() {
    Mono.never()
      .doOnSuccess(x -> System.out.println("SUCCESS!"))
      .subscribe(System.out::println);
  }

  @Test
  void fooMono() {
    Mono.just("foo")
      .subscribe(System.out::println);
  }

  @Test
  void errorMono() {
    Mono.error(new IllegalStateException())
      .doOnError(err -> System.out.println("----- Error: " + err))
      .subscribe();
  }
}
