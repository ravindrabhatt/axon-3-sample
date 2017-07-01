package org.kun.domain;

import org.kun.domain.command.CreateMessageCommand;
import org.kun.domain.event.MessageCreatedEvent;
import org.kun.model.IdGenerator;
import org.kun.model.Identifier;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;

public class MessageTest {
  IdGenerator idGenerator;
  private FixtureConfiguration<Message> testFixture;

  @Before
  public void setUp() throws Exception {
    idGenerator = new IdGenerator();
    testFixture = new AggregateTestFixture<>(Message.class);
  }

  @Test
  public void createMessage() throws Exception {
    Identifier identifier = idGenerator.generate();
    String message = "message";

    testFixture.givenNoPriorActivity()
      .when(new CreateMessageCommand(identifier, message))
      .expectEvents(new MessageCreatedEvent(identifier.getId(), message));
  }
}