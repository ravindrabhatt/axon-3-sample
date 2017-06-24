package com.rbhatt.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rbhatt.model.ErrorMessage;
import com.rbhatt.model.Message;
import com.rbhatt.repository.MessageRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;

import static com.rbhatt.web.Controller.MESSAGE_NOT_PRESENT;
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
  private MessageRepository messages;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;


  @Before
  public void setUp() throws Exception {
    objectMapper = new ObjectMapper();
    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter = new MappingJackson2HttpMessageConverter(objectMapper);
    mockMvc = standaloneSetup(new Controller(messages))
      .setMessageConverters(mappingJackson2HttpMessageConverter)
      .build();
  }

  @Test
  public void shouldReturnMessage() throws Exception {
    String id = "12345";
    Message expectedMessage = new Message(id, "hello world!");

    when(messages.findOne(id)).thenReturn(expectedMessage);

    mockMvc.perform(get("/messages/" + id).accept(APPLICATION_JSON))
      .andExpect(status().isOk())
      .andExpect(content().json(objectMapper.writeValueAsString(expectedMessage)));
  }

  @Test
  public void shouldCreateMessage() throws Exception {
    String id = "12345";
    Message expectedMessage = new Message(id, "hello world!");

    when(messages.save(expectedMessage)).thenReturn(expectedMessage);

    mockMvc.perform(post("/messages/add")
      .content(objectMapper.writeValueAsString(expectedMessage))
      .contentType(APPLICATION_JSON))
      .andExpect(status().isAccepted())
      .andExpect(content().json(objectMapper.writeValueAsString(expectedMessage)));
  }

  @Test
  public void shouldReturnNotFoundIfMessageNotPresent() throws Exception {
    String id = "12345";
    ErrorMessage expectedError = new ErrorMessage(id, MESSAGE_NOT_PRESENT);

    when(messages.findOne(id)).thenReturn(null);

    mockMvc.perform(get("/messages/" + id).accept(APPLICATION_JSON))
      .andExpect(status().isNotFound())
      .andExpect(content().json(objectMapper.writeValueAsString(expectedError)));
  }
}