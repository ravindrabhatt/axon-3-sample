package org.kun.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.axonframework.eventhandling.EventMessage;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.spring.messaging.OutboundEventMessageChannelAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;

@Configuration
public class MessagingConfiguration {

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
  public OutboundEventMessageChannelAdapter outboundEventMessageChannelAdapter(EventStore eventStore, JmsTemplate jmsTemplate) {
    return new OutboundEventMessageChannelAdapter(eventStore, new MessageChannel() {
      @Override
      public boolean send(Message<?> message) {
        jmsTemplate.convertAndSend(message);
        return true;
      }

      @Override
      public boolean send(Message<?> message, long timeout) {
        jmsTemplate.convertAndSend(message);
        return true;
      }
    }) {
      @Override
      protected Message<?> transform(EventMessage<?> event) {
        return new GenericMessage<EventMessage>(event);
      }
    };
  }


}
