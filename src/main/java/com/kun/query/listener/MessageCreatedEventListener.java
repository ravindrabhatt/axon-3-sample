package org.kun.query.listener;

import org.kun.domain.event.MessageCreatedEvent;
import org.kun.model.MessageView;
import org.kun.repository.MessageRepository;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
class MessageCreatedEventListener {

  private MessageRepository messages;

  @Autowired
  MessageCreatedEventListener(MessageRepository messages) {
    this.messages = messages;
  }

  @EventHandler
  void handle(MessageCreatedEvent event) {
    messages.save(new MessageView(event.getIdentifier(), event.getData()));
  }
}
