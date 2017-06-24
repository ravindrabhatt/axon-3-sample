package com.rbhatt.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Message {
  @Id
  String id;
  String data;

  private Message() {
  }

  public Message(String id, String data) {
    this.id = id;
    this.data = data;
  }

  public String getData() {
    return data;
  }

  public String getId() {
    return id;
  }
}

