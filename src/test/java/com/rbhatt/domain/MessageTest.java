package com.rbhatt.domain;

import com.rbhatt.domain.command.CreateMessageCommand;
import com.rbhatt.domain.event.MessageCreatedEvent;
import com.rbhatt.model.IdGenerator;
import com.rbhatt.model.Identifier;
import org.axonframework.test.aggregate.AggregateTestFixture;
import org.axonframework.test.aggregate.FixtureConfiguration;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

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