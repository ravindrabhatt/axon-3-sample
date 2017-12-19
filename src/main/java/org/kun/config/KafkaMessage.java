package org.kun.config;

import org.axonframework.messaging.MetaData;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;

import java.util.Collections;

public class KafkaMessage implements Message {
  private String identifier;
  private Object payload;
  private MetaData metaData;
  private Class<?> payloadType;

  public KafkaMessage() {
  }

  public KafkaMessage(String identifier, Object payload, MetaData metaData, Class<?> payloadType) {
    this.identifier = identifier;
    this.payload = payload;
    this.metaData = metaData;
    this.payloadType = payloadType;
  }

  @Override
  public Object getPayload() {
    return payload;
  }

  @Override
  public MessageHeaders getHeaders() {
    return new MessageHeaders(Collections.emptyMap());
  }

  public String getIdentifier() {
    return identifier;
  }

  public MetaData getMetaData() {
    return metaData;
  }

  public Class<?> getPayloadType() {
    return payloadType;
  }
}
