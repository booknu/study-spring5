package com.booknu.reactor.ch2;

import java.time.Duration;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Flux;

public class FluxExample {

  @SneakyThrows
  @Test
  void fluxExample() {
    Flux.just(1, 2, 3, 4, 5) // flux 소스 생성
      .delayElements(Duration.ofMillis(100)) // 아이템을 보내기 전 딜레이를 줌
      .doOnNext(e -> System.out.println("doOnNext: " + e)) // 아이템을 보낼 때 트리거
      .map(d -> d * 100)
      .take(3) // 첫 n 개의 원소만 받아들임
      .onErrorResume(err -> Flux.just(-100, -100)) // 에러가 발생하면 fallback publisher 를 subscribe
      .doAfterTerminate(() -> System.out.println("AFTER TERMINATE")) // flux 가 끝난 후 할 일 등록
      .subscribe(e -> System.out.println("subscribe: " + e)); // 트리거: 시퀀스의 모든 원소를 consume

    Thread.sleep(400); // 논블로킹으로 작업이 진행되기 때문에 main thread 가 끝나지 않도록 대기
  }
}
