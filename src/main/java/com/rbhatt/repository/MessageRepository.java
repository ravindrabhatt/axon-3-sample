package com.rbhatt.repository;

import com.rbhatt.model.MessageView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface MessageRepository extends MongoRepository<MessageView, String> {
}
