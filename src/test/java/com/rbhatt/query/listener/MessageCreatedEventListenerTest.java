package com.rbhatt.query.listener;

import com.rbhatt.domain.event.MessageCreatedEvent;
import com.rbhatt.model.MessageView;
import com.rbhatt.repository.MessageRepository;
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