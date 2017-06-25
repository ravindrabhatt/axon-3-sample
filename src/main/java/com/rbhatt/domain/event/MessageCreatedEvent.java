package com.rbhatt.domain.event;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;

public class MessageCreatedEvent {
  private String identifier;
  private String data;

  public MessageCreatedEvent() {
  }

  public MessageCreatedEvent(String identifier, String data) {
    this.identifier = identifier;
    this.data = data;
  }

  public String getIdentifier() {
    return identifier;
  }

  public String getData() {
    return data;
  }

  @Override
  public boolean equals(Object o) {
    return reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return reflectionHashCode(this);
  }
}
