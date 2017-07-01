package org.kun.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;

@Document(collection = "messages")
public class MessageView {
  @Id
  String id;
  String data;

  private MessageView() {
  }

  public MessageView(String id, String data) {
    this.id = id;
    this.data = data;
  }

  public String getData() {
    return data;
  }

  public String getId() {
    return id;
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

