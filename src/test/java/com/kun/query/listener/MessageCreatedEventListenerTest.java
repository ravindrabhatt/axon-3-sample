package org.kun.query.listener;

import org.kun.domain.event.MessageCreatedEvent;
import org.kun.model.MessageView;
import org.kun.repository.MessageRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MessageCreatedEventListenerTest {

  @Mock
  private MessageRepository messages;

  @Test
  public void shouldSaveData() throws Exception {
    MessageCreatedEventListener listener = new MessageCreatedEventListener(messages);
    listener.handle(new MessageCreatedEvent("id", "data"));

    Mockito.verify(messages).save(new MessageView("id", "data"));

  }
}