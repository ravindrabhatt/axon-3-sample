package org.kun.repository;

import org.kun.model.MessageView;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface MessageRepository extends MongoRepository<MessageView, String> {
}
