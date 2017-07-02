package org.kun.domain;

import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.commandhandling.model.AggregateRoot;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.kun.domain.command.CreateMessageCommand;
import org.kun.domain.event.MessageCreatedEvent;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

@AggregateRoot
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
