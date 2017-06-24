package com.rbhatt.web;

import com.rbhatt.model.ErrorMessage;
import com.rbhatt.model.Message;
import com.rbhatt.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/messages")
class Controller {

  static final String MESSAGE_NOT_PRESENT = "Message not present.";
  static final String COULD_NOT_SAVE_MESSAGE = "Could not save message";
  private MessageRepository messages;

  @Autowired
  Controller(MessageRepository messages) {
    this.messages = messages;
  }

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity getMessage(@PathVariable("id") String id) {
    Message message = messages.findOne(id);

    if (message == null) {
      return new ResponseEntity(new ErrorMessage(id, MESSAGE_NOT_PRESENT), HttpStatus.NOT_FOUND);
    } else {
      return ResponseEntity.ok(message);
    }
  }

  @RequestMapping(value = "/add", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity createMessage(@RequestBody Message message) {

    Message savedMessage = messages.save(message);

    if (message == null) {
      return new ResponseEntity(new ErrorMessage(COULD_NOT_SAVE_MESSAGE), HttpStatus.INTERNAL_SERVER_ERROR);
    } else {
      return ResponseEntity.accepted().body(savedMessage);
    }
  }
}
