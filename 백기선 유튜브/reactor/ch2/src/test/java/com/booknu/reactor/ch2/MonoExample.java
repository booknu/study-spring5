package com.booknu.reactor.ch2;

import java.time.Duration;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Mono;

public class MonoExample {

  @SneakyThrows
  @Test
  void just() {
    Mono.just(1)
      .map(x -> (long) x * 100)
      .subscribe(System.out::println);
  }

  /**
   * 원본 Mono 와 다른 Mono 중 먼저 시그널이 오는 원소를 내보냄
   */
  @SneakyThrows
  @Test
  void or() {
    var m1 = Mono.delay(Duration.ofMillis(200)).thenReturn(200);
    var m2 = Mono.delay(Duration.ofMillis(100)).thenReturn(100);

    m1.or(m2)
        .subscribe(System.out::println);

    Thread.sleep(400);
  }

  @SneakyThrows
  @Test
  void firstWithValue() {
    var m1 = Mono.delay(Duration.ofMillis(200)).thenReturn(200);
    var m2 = Mono.delay(Duration.ofMillis(100)).thenReturn(100);

    Mono.firstWithValue(m1, m2)
      .subscribe(System.out::println);

    Thread.sleep(400);
  }
}
