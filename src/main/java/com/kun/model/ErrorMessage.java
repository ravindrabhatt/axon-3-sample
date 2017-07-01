package org.kun.model;

public class ErrorMessage {
  private String id;
  private String msg;

  private ErrorMessage() {
  }

  public ErrorMessage(String id, String msg) {
    this.id = id;
    this.msg = msg;
  }

  public ErrorMessage(String msg) {
    this.msg = msg;
  }

  public String getId() {
    return id;
  }

  public String getMsg() {
    return msg;
  }
}
