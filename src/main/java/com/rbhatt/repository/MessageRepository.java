package com.rbhatt.repository;

import com.rbhatt.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface MessageRepository extends MongoRepository<Message, String> {
}
