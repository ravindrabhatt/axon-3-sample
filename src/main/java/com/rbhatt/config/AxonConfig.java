package com.rbhatt.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.rbhatt.domain.Message;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventhandling.EventBus;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.messaging.interceptors.BeanValidationInterceptor;
import org.axonframework.mongo.eventsourcing.eventstore.DefaultMongoTemplate;
import org.axonframework.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.mongo.eventsourcing.eventstore.MongoTemplate;
import org.axonframework.mongo.eventsourcing.eventstore.documentperevent.DocumentPerEventStorageStrategy;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.spring.config.AxonConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import static java.util.Collections.singletonList;

@Configuration
@PropertySource("classpath:application.properties")
public class AxonConfig {
  @Value("${mongo.host.name}")
  private String mongoHost;
  @Value("${mongo.host.port}")
  private Integer mongoPort;
  @Value("${axon.mongo.user.name}")
  private String mongoUser;
  @Value("${axon.mongo.user.password}")
  private String mongoPass;
  @Value("${axon.application.databaseName}")
  private String databaseName;
  @Value("${axon.application.eventsCollectionName}")
  private String eventsCollectionName;
  @Value("${axon.application.snapshotCollectionName}")
  private String snapshotCollectionName;

  @Autowired
  private AxonConfiguration axonConfiguration;

  @Autowired
  private EventBus eventBus;

  @Bean
  public Repository<Message> messages() {
    return axonConfiguration.repository(Message.class);
  }

  @Autowired
  public void configure(@Qualifier("localSegment") SimpleCommandBus simpleCommandBus) {
    simpleCommandBus.registerDispatchInterceptor(new BeanValidationInterceptor<>());
  }

  @Bean
  JacksonSerializer axonJsonSerializer() {
    return new JacksonSerializer();
  }

  @Bean(name = "axonMongoTemplate")
  MongoTemplate axonMongoTemplate() {
    MongoCredential credential = MongoCredential.createCredential(mongoUser, databaseName, mongoPass.toCharArray());
    ServerAddress serverAddress = new ServerAddress(mongoHost, mongoPort);

    MongoClient mongoClient = new MongoClient(serverAddress, singletonList(credential));

    MongoTemplate template = new DefaultMongoTemplate(mongoClient, databaseName, eventsCollectionName, snapshotCollectionName);
    return template;
  }

  @Bean
  public EventStorageEngine eventStore(MongoTemplate axonMongoTemplate) {
    return new MongoEventStorageEngine(
      axonJsonSerializer(), null, axonMongoTemplate(), new DocumentPerEventStorageStrategy());
  }
}
