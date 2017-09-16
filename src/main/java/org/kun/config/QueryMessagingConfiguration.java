package org.kun.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.jms.Jms;
import org.springframework.messaging.MessageChannel;

@Configuration
public class QueryMessagingConfiguration {
  private static final String QUERY_CHANNEL = "query_channel";
  private static final String ORDER_QUEUE = "order-queue";

  @Bean
  public IntegrationFlow integrationFlow(ActiveMQConnectionFactory connectionFactory,
                                         @Qualifier(value = QUERY_CHANNEL) MessageChannel messageChannel,
                                         JmsMessageConverter jmsMessageConverter) {
    return IntegrationFlows.from(Jms
      .messageDrivenChannelAdapter(connectionFactory)
      .configureListenerContainer(c -> c.sessionTransacted(true))
      .destination(ORDER_QUEUE)
      .jmsMessageConverter(jmsMessageConverter).get())
      .channel(messageChannel)
      .get();
  }

  @Bean(name = QUERY_CHANNEL)
  public PublishSubscribeChannel messageChannel() {
    return new PublishSubscribeChannel();
  }
}
