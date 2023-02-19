package com.booknu.reactor.ch4;

import java.util.List;
import java.util.function.Supplier;
import java.util.random.RandomGenerator;
import java.util.stream.Collectors;
import lombok.SneakyThrows;
import org.junit.jupiter.api.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class TransformPractice {

  @Test
  void capitalizeOne() {
    User user = User.JESSE;

    Mono<User> mono = Mono.just(user)
      .map(User::capitalize);

    StepVerifier.create(mono)
      .expectNext(user.capitalize())
      .verifyComplete();
  }

  @Test
  void capitalizeMany() {
    List<User> users = List.of(User.JESSE, User.SAUL, User.SKYLER, User.WALTER);

    Flux<User> flux = Flux.fromIterable(users)
      .map(User::capitalize);

    StepVerifier.create(flux)
      .expectNextSequence(users.stream().map(User::capitalize).toList())
      .verifyComplete();
  }

  @Test
  void asyncCapitalizeMany() {
    var tt = Flux.just(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15)
      .window(3)
      .flatMap(x -> x.map(this::delayedFunction))
      .doOnNext(System.out::println)
      .blockLast();

    System.out.println(tt);
  }

  @SneakyThrows
  public List delayedFunction(Integer x) {
    Thread.sleep(RandomGenerator.getDefault().nextLong(400, 1000));
    return List.of(x * 100, Thread.currentThread().getName());
  }
}
