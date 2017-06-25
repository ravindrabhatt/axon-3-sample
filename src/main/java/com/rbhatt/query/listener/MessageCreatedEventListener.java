package com.rbhatt.query.listener;

import com.rbhatt.domain.event.MessageCreatedEvent;
import com.rbhatt.model.MessageView;
import com.rbhatt.repository.MessageRepository;
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
