package com.rbhatt.domain.command;

import com.rbhatt.model.Identifier;
import org.axonframework.commandhandling.TargetAggregateIdentifier;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.apache.commons.lang3.builder.HashCodeBuilder.reflectionHashCode;

public class CreateMessageCommand {
  @TargetAggregateIdentifier
  private Identifier identifier;
  private String data;

  private CreateMessageCommand() {
  }

  public CreateMessageCommand(Identifier identifier, String data) {
    this.identifier = identifier;
    this.data = data;
  }

  public Identifier getIdentifier() {
    return identifier;
  }

  public String getData() {
    return data;
  }

  @Override
  public boolean equals(Object o) {
    return reflectionEquals(this, o);
  }

  @Override
  public int hashCode() {
    return reflectionHashCode(this);
  }
}
