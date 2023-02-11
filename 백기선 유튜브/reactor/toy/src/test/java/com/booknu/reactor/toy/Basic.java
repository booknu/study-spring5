package com.booknu.reactor.toy;

import java.time.Duration;
import java.util.List;
import java.util.function.Consumer;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Flux;

public class Basic {

  @Test
  void simpleExample() {
    var flux = Flux.just("1", "2"); // flux 소스 생성

    var flux2 = flux.map(x -> x + " (2)"); //

    Consumer<String> print = x -> {
      System.out.println(x);
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    };

    flux.subscribe(x -> print.accept("pub1 : " + x));
    flux2.subscribe(x -> print.accept("pub2 : " + x));
  }
}
