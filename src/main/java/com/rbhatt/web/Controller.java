package com.rbhatt.web;

import com.rbhatt.domain.command.CreateMessageCommand;
import com.rbhatt.model.IdGenerator;
import com.rbhatt.model.Identifier;
import com.rbhatt.model.MessageView;
import com.rbhatt.repository.MessageRepository;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/messages")
class Controller {

  private CommandGateway gateway;
  private MessageRepository messages;
  private IdGenerator idGenerator;

  @Autowired
  Controller(CommandGateway gateway, MessageRepository messages, IdGenerator idGenerator) {
    this.gateway = gateway;
    this.messages = messages;
    this.idGenerator = idGenerator;
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity getMessage(@PathVariable("id") String id) {
    MessageView messageView = messages.findOne(id);

    if (messageView == null) {
      return ResponseEntity.notFound().build();
    } else {
      return ResponseEntity.ok(messageView);
    }
  }

  @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity createMessage(@RequestBody Request request) {
    Identifier identifier = idGenerator.generate();

    gateway.send(new CreateMessageCommand(identifier, request.getData()));

    return ResponseEntity.accepted().body(identifier);
  }
}
