package org.kun.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.axonframework.eventsourcing.GenericDomainEventMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.support.SimpleJmsHeaderMapper;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@Component
public class JmsMessageConverter implements MessageConverter {

  @Autowired
  private ObjectMapper objectMapper;

  @Override
  public Message toMessage(Object o, Session session) throws JMSException, MessageConversionException {
    GenericMessage message = (GenericMessage) o;
    ActiveMQTextMessage activeMQTextMessage = new ActiveMQTextMessage();

    try {
      GenericDomainEventMessage event = (GenericDomainEventMessage) ((GenericMessage) o).getPayload();
      activeMQTextMessage.setText(objectMapper.writeValueAsString(event.getPayload()));
      activeMQTextMessage.setJMSMessageID(event.getAggregateIdentifier());
      activeMQTextMessage.setJMSType(event.getPayloadType().getName());
      new SimpleJmsHeaderMapper().fromHeaders(message.getHeaders(), activeMQTextMessage);

    } catch (JsonProcessingException e) {
      e.printStackTrace();
    }
    return activeMQTextMessage;
  }

  @Override
  public Object fromMessage(Message message) throws JMSException, MessageConversionException {
    ActiveMQTextMessage activeMQTextMessage = (ActiveMQTextMessage) message;
    Object domainEventMessage = null;
    try {
      domainEventMessage = objectMapper.readValue(activeMQTextMessage.getText(), Class.forName(activeMQTextMessage.getJMSType()));
    } catch (Exception e) {

    }
    return domainEventMessage;
  }
}
