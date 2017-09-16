package org.kun.query.listener;

import org.kun.domain.event.MessageCreatedEvent;
import org.kun.model.MessageView;
import org.kun.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

@Component
public class MessageCreatedEventListener {

  private MessageRepository messages;

  @Autowired
  public MessageCreatedEventListener(MessageRepository messages) {
    this.messages = messages;
  }

  @ServiceActivator(inputChannel = "query_channel")
  public void handle(MessageCreatedEvent event) {
    messages.save(new MessageView(event.getIdentifier(), event.getData()));
  }
}
