package com.booknu.reactor.ch4;

import java.time.Duration;
import java.util.Random;
import java.util.random.RandomGenerator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.ToString;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@Getter
@ToString
public class User {

  public static final User SKYLER = new User("swhite", "Skyler", "White");
  public static final User JESSE = new User("jpinkman", "Jesse", "Pinkman");
  public static final User WALTER = new User("wwhite", "Walter", "White");
  public static final User SAUL = new User("sgoodman", "Saul", "Goodman");

  private final String username;

  private final String firstname;

  private final String lastname;

  public User(String username, String firstname, String lastname) {
    this.username = username;
    this.firstname = firstname;
    this.lastname = lastname;
  }

  public User capitalize() {
    return new User(username.toUpperCase(), firstname.toUpperCase(), lastname.toUpperCase());
  }
}
