package com.rbhatt.web;

class Request {
  private String data;

  Request(String data) {
    this.data = data;
  }

  private Request() {
  }

  public String getData() {
    return data;
  }
}
