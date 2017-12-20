package org.kun.config;


import org.springframework.kafka.support.serializer.JsonDeserializer;

public class MessageDeserializer extends JsonDeserializer<String> {

  public MessageDeserializer() {
  }

  @Override
  public String deserialize(String topic, byte[] data) {
    try {
      return new String(data);
    } catch (Exception e) {
      throw new RuntimeException("Failed to deserialize payload");
    }
  }
}
