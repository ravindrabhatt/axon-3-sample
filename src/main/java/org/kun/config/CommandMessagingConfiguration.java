package org.kun.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.spring.messaging.OutboundEventMessageChannelAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CommandMessagingConfiguration {

  private static final String DEFAULT_BROKER_URL = "tcp://localhost:61616";

  private static final String ORDER_QUEUE = "order-queue";

  @Bean
  public ActiveMQConnectionFactory connectionFactory() {
    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
    connectionFactory.setBrokerURL(DEFAULT_BROKER_URL);
    return connectionFactory;
  }

  @Bean
  public JmsTemplate jmsTemplate(JmsMessageConverter jmsMessageConverter) {
    JmsTemplate template = new JmsTemplate();
    template.setConnectionFactory(connectionFactory());
    template.setMessageConverter(jmsMessageConverter);
    template.setDefaultDestinationName(ORDER_QUEUE);
    return template;
  }

  @Bean
  public KafkaTemplate kafkaTemplate(ProducerFactory<String, GenericMessage> producerFactory,
                                     @Value("${kafka.topic}") String topic,
                                     @Value("${kafka.server}") String kafkaServer) {

    KafkaTemplate<String, GenericMessage> kafkaTemplate = new KafkaTemplate<String, GenericMessage>(producerFactory);
    kafkaTemplate.setMessageConverter(new CustomMessageConverter());
    kafkaTemplate.setDefaultTopic(topic);
    return kafkaTemplate;
  }

  @Bean
  public ProducerFactory<String, GenericMessage> producerFactory(ObjectMapper objectMapper,
                                                                 @Value("${kafka.server}") String kafkaServer) {
    Map<String, Object> props = new HashMap<String, Object>();
    props.put("bootstrap.servers", kafkaServer);
    props.put("acks", "all");
    props.put("retries", 0);
    props.put("batch.size", 1);
    props.put("linger.ms", 1);
    props.put("buffer.memory", 33554432);
    props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
    props.put("max.in.flight.requests.per.connection", 1);
    props.put("client.id", "command");

    DefaultKafkaProducerFactory<String, GenericMessage> producerFactory = new DefaultKafkaProducerFactory<String, GenericMessage>(props);
    producerFactory.setValueSerializer(new JsonSerializer<>(objectMapper));
    return producerFactory;
  }

  @Bean
  public OutboundEventMessageChannelAdapter outboundEventMessageChannelAdapter(EventStore eventStore, KafkaTemplate kafkaTemplate) {
    return new OutboundEventMessageChannelAdapter(eventStore, new MessageChannel() {
      @Override
      public boolean send(Message<?> message) {
        kafkaTemplate.send(message);
        return true;
      }

      @Override
      public boolean send(Message<?> message, long timeout) {
        kafkaTemplate.send(message);
        return true;
      }
    }) {
      @Override
      protected Message<?> transform(EventMessage<?> event) {
        return new KafkaMessage(event.getIdentifier(), event.getPayload(), event.getMetaData(), event.getPayloadType());
      }
    };
  }


}
