package com.rbhatt.model;

import org.springframework.stereotype.Component;

import static java.util.UUID.randomUUID;

@Component
public class IdGenerator {

  public Identifier generate() {
    return new Identifier(randomUUID().toString());
  }
}
