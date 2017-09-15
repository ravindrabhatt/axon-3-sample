package org.kun.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kun.domain.command.CreateMessageCommand;
import org.kun.model.IdGenerator;
import org.kun.model.Identifier;
import org.kun.model.MessageView;
import org.kun.repository.MessageRepository;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;


@RunWith(MockitoJUnitRunner.class)
public class ControllerTest {

  @Mock
  IdGenerator idGenerator;
  @Mock
  private MessageRepository messages;
  @Mock
  private CommandGateway gateway;
  private MockMvc mockMvc;
  private ObjectMapper objectMapper;


  @Before
  public void setUp() throws Exception {
    objectMapper = new ObjectMapper();
    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter(objectMapper);
    mockMvc = standaloneSetup(new Controller(gateway, messages, idGenerator))
      .setMessageConverters(mappingJackson2HttpMessageConverter)
      .build();
  }

  @Test
  public void shouldReturnMessage() throws Exception {
    String id = "12345";
    MessageView expectedMessage = new MessageView(id, "hello world");

    when(messages.findOne(id)).thenReturn(expectedMessage);

    mockMvc.perform(get("/messages/" + id).accept(APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().json(objectMapper.writeValueAsString(expectedMessage)));
  }

  @Test
  public void shouldCreateMessage() throws Exception {
    Request request = new Request("hello world!");
    String id = UUID.randomUUID().toString();
    Identifier identifier = new Identifier(id);

    when(idGenerator.generate()).thenReturn(identifier);

    mockMvc.perform(post("/messages/add")
      .content(objectMapper.writeValueAsString(request))
      .contentType(APPLICATION_JSON))
      .andExpect(status().isAccepted())
      .andExpect(content().json(objectMapper.writeValueAsString(identifier)));

    verify(gateway).send(new CreateMessageCommand(identifier, request.getData()));
  }

  @Test
  public void shouldReturnNotFoundIfMessageNotPresent() throws Exception {
    String id = "12345";

    when(messages.findOne(id)).thenReturn(null);

    mockMvc.perform(get("/messages/" + id).accept(APPLICATION_JSON))
      .andExpect(status().isNotFound());
  }
}