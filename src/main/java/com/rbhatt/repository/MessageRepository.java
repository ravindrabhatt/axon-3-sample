package com.rbhatt.repository;

import com.rbhatt.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface MessageRepository extends JpaRepository<Message, String> {
}
