package com.booknu.reactor.ch4;

import java.util.List;
import java.util.random.RandomGenerator;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

public class FlatMapPractice {

  @Test
  @DisplayName("기본적인 flatMap: 작업이 병렬로 실행되지 않음")
  void flatMap() {
    Flux.range(0, 15)
      .window(3) // flux 를 3개 단위로 자름 [[0, 1, 2], [3, 4, 5], ...]
      .flatMap(x -> x.map(this::delayedFunction))
      .doOnNext(System.out::println)
      .blockLast();
  }

  @Test
  @DisplayName("subscribeOn parallel 사용: 작업이 병렬로 실행되나 순서에 대한 보장이 없음.")
  void flatMap_parallelSubscribe() {
    Flux.range(0, 15)
      .window(3)
      .flatMap(x -> x.map(this::delayedFunction).subscribeOn(Schedulers.parallel()))
      .doOnNext(System.out::println)
      .blockLast();
  }

  @Test
  @DisplayName("concatMap: 아이템을 순차적으로 처리하나 subscribeOn parallel 이 의미가 없어짐")
  void concatMap_parallelSubscribe() {
    Flux.range(0, 15)
      .window(3)
      .concatMap(x -> x.map(this::delayedFunction).subscribeOn(Schedulers.parallel()))
      .doOnNext(System.out::println)
      .blockLast();
  }

  @Test
  @DisplayName("flatMapSequential: subscribeOn parallel 로 병렬로 트리거 한 후 그 결과를 순차적으로 가져옴")
  void flatMapSequential_parallelSubscribe() {
    Flux.range(0, 15)
      .window(3)
      .flatMapSequential(x -> x.map(this::delayedFunction).subscribeOn(Schedulers.parallel()))
      .doOnNext(System.out::println)
      .blockLast();
  }

  @SneakyThrows
  private List delayedFunction(int x) {
    Thread.sleep(500);

    return List.of(x, Thread.currentThread().getName());
  }
}
