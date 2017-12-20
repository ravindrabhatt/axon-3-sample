package org.kun.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.hibernate.validator.internal.util.CollectionHelper;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.KafkaNull;
import org.springframework.kafka.support.converter.MessagingMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import java.lang.reflect.Type;
import java.util.Map;

public class CustomMessageConverter extends MessagingMessageConverter {

  private ObjectMapper objectMapper;

  public CustomMessageConverter(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public CustomMessageConverter() {
  }

  @Override
  public Message<?> toMessage(ConsumerRecord<?, ?> record, Acknowledgment acknowledgment, Type type) {

    Map<String, Object> rawHeaders = CollectionHelper.newHashMap();
    rawHeaders.put(KafkaHeaders.RECEIVED_MESSAGE_KEY, record.key());
    rawHeaders.put(KafkaHeaders.RECEIVED_TOPIC, record.topic());
    rawHeaders.put(KafkaHeaders.RECEIVED_PARTITION_ID, record.partition());
    rawHeaders.put(KafkaHeaders.OFFSET, record.offset());

    if (acknowledgment != null) {
      rawHeaders.put(KafkaHeaders.ACKNOWLEDGMENT, acknowledgment);
    }

    return MessageBuilder.createMessage(extractAndConvertValue(record, type), new MessageHeaders(rawHeaders));
  }

  @Override
  public ProducerRecord<?, ?> fromMessage(Message<?> message, String defaultTopic) {
    MessageHeaders headers = message.getHeaders();
    String topic = headers.get(KafkaHeaders.TOPIC, String.class);
    Integer partition = headers.get(KafkaHeaders.PARTITION_ID, Integer.class);
    Object key = headers.get(KafkaHeaders.MESSAGE_KEY);
    return new ProducerRecord(topic == null ? defaultTopic : topic, partition, key, message);
  }

  protected Object extractAndConvertValue(ConsumerRecord<?, ?> record, Type type) {
    Object result = null;

    try {
      JsonNode jsonNode = objectMapper.readTree(record.value().toString());
      result = record.value() == null ? KafkaNull.INSTANCE : objectMapper.readValue(getPayload(jsonNode), getPayloadType(jsonNode));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return result;
  }

  private Class<?> getPayloadType(JsonNode jsonNode) throws ClassNotFoundException {
    return Class.forName(jsonNode.get("payloadType").textValue());
  }

  private String getPayload(JsonNode jsonNode) throws JsonProcessingException {
    return objectMapper.writeValueAsString(jsonNode.get("payload"));
  }
}
