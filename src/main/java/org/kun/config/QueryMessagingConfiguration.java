package org.kun.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.kafka.Kafka;
import org.springframework.integration.endpoint.MessageProducerSupport;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.TopicPartitionInitialOffset;
import org.springframework.messaging.MessageChannel;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;

import java.util.HashMap;

import static org.springframework.kafka.listener.adapter.AbstractRetryingMessageListenerAdapter.CONTEXT_ACKNOWLEDGMENT;

@Configuration
public class QueryMessagingConfiguration {
  private static final String QUERY_CHANNEL = "query_channel";

  @Bean
  public IntegrationFlow integrationFlow(@Qualifier(value = QUERY_CHANNEL) MessageChannel messageChannel,
                                         @Qualifier("kafkaMessageAdapter") MessageProducerSupport kafkaMessageDrivenChannelAdapter) {
    return IntegrationFlows
      .from(kafkaMessageDrivenChannelAdapter)
      .channel(messageChannel)
      .get();
  }


  @Bean("kafkaMessageAdapter")
  public MessageProducerSupport kafkaMessageAdapter(KafkaMessageListenerContainer<String, String> listenerContainer,
                                                    ObjectMapper objectMapper) {
    return Kafka
      .messageDrivenChannelAdapter(listenerContainer)
      .retryTemplate(new RetryTemplate())
      .recoveryCallback(new RecoveryCallback<Void>() {
        @Override
        public Void recover(RetryContext retryContext) throws Exception {
          Acknowledgment acknowledgment = (Acknowledgment) retryContext.getAttribute(CONTEXT_ACKNOWLEDGMENT);
          acknowledgment.acknowledge();
          return null;
        }
      })
      .messageConverter(new MessageConverter(objectMapper))
      .get();
  }

  @Bean
  public KafkaMessageListenerContainer<String, String> listenerContainer(@Value("${kafka.topic}") String topic,
                                                                         ConsumerFactory<String, String> kafkaConsumerFactory) {
    ContainerProperties containerProperties = new ContainerProperties(new TopicPartitionInitialOffset(topic, 0));
    containerProperties.setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL);

    return new KafkaMessageListenerContainer<>(kafkaConsumerFactory, containerProperties);
  }

  @Bean
  public ConsumerFactory<String, String> kafkaConsumerFactory(@Value("${kafka.topic}") String topic,
                                                              @Value("${kafka.server}") String kafkaServer,
                                                              ObjectMapper objectMapper) {
    HashMap<String, Object> props = new HashMap<>();
    props.put("bootstrap.servers", kafkaServer);
    props.put("group.id", topic);
    props.put("enable.auto.commit", false);
    return new DefaultKafkaConsumerFactory<String, String>(props, new StringDeserializer(), new MessageDeserializer());
  }

  @Bean(name = QUERY_CHANNEL)
  public PublishSubscribeChannel messageChannel() {
    return new PublishSubscribeChannel();
  }
}
