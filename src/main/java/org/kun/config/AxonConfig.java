package org.kun.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.axonframework.commandhandling.AggregateAnnotationCommandHandler;
import org.axonframework.commandhandling.CommandBus;
import org.axonframework.commandhandling.SimpleCommandBus;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.commandhandling.gateway.DefaultCommandGateway;
import org.axonframework.commandhandling.model.Repository;
import org.axonframework.eventsourcing.EventSourcingRepository;
import org.axonframework.eventsourcing.eventstore.EmbeddedEventStore;
import org.axonframework.eventsourcing.eventstore.EventStorageEngine;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.mongo.eventsourcing.eventstore.DefaultMongoTemplate;
import org.axonframework.mongo.eventsourcing.eventstore.MongoEventStorageEngine;
import org.axonframework.mongo.eventsourcing.eventstore.MongoTemplate;
import org.axonframework.mongo.eventsourcing.eventstore.documentperevent.DocumentPerEventStorageStrategy;
import org.axonframework.serialization.json.JacksonSerializer;
import org.axonframework.spring.config.CommandHandlerSubscriber;
import org.kun.domain.Message;
import org.kun.query.listener.MessageCreatedEventListener;
import org.kun.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import static java.util.Collections.singletonList;

@Configuration
@PropertySource("classpath:application.properties")
public class AxonConfig {
  @Autowired
  MessageCreatedEventListener messageCreatedEventListener;
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

  @Bean
  public CommandHandlerSubscriber commandHandlerSubscriber() {
    return new CommandHandlerSubscriber();
  }

  @Bean
  public CommandBus commandBus() {
    return new SimpleCommandBus();
  }

  @Bean
  public CommandGateway commandGateway(CommandBus commandBus) {
    return new DefaultCommandGateway(commandBus);
  }


  @Bean
  public EventStore eventStore(EventStorageEngine eventStorageEngine, MessageRepository messageRepository) {
    return new EmbeddedEventStore(eventStorageEngine);
  }

  @Bean
  public Repository<Message> messages(EventStore eventStore) {
    EventSourcingRepository<Message> messageEventSourcingRepository = new EventSourcingRepository<>(Message.class, eventStore);
    return messageEventSourcingRepository;
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
  public EventStorageEngine eventStorageEngine(MongoTemplate axonMongoTemplate) {
    return new MongoEventStorageEngine(
      axonJsonSerializer(), null, axonMongoTemplate(), new DocumentPerEventStorageStrategy());
  }

  @Bean
  public AggregateAnnotationCommandHandler<Message> messageCommandHandler(EventStore eventStore) {
    return new AggregateAnnotationCommandHandler<>(Message.class, messages(eventStore));
  }
}
