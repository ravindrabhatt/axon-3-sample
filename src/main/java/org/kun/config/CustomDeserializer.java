package org.kun.config;


import org.springframework.kafka.support.serializer.JsonDeserializer;

public class CustomDeserializer extends JsonDeserializer<String> {

  public CustomDeserializer() {
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
