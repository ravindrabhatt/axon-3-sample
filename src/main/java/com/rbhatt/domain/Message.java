package com.rbhatt.domain;

import com.rbhatt.domain.command.CreateMessageCommand;
import com.rbhatt.domain.event.MessageCreatedEvent;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;


@Aggregate
public class Message {
  @AggregateIdentifier
  private String id;
  private String data;

  private Message() {
  }

  @CommandHandler
  public Message(CreateMessageCommand command) {
    apply(new MessageCreatedEvent(command.getIdentifier().getId(), command.getData()));
  }

  @EventSourcingHandler
  public void on(MessageCreatedEvent event) {
    this.id = event.getIdentifier();
    this.data = event.getData();
  }
}
