package org.kun.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.support.serializer.JsonDeserializer;

public class CustomDeserializer extends JsonDeserializer {

  public CustomDeserializer(ObjectMapper objectMapper) {
    super(Object.class, objectMapper);
  }

  @Override
  public Object deserialize(String topic, byte[] data) {
    try {
      return objectMapper.readTree(data);
    } catch (Exception e) {
      throw new RuntimeException("Failed to deserialize payload");
    }
  }


}
