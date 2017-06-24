package com.rbhatt.config;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;

import static java.util.Collections.singletonList;

@Configuration
public class MongoConfiguration {
  @Bean
  MongoDbFactory mongoDbFactory(String mongoUser,
                                String mongoPass,
                                String databaseName,
                                String mongoHost,
                                Integer mongoPort) throws Exception {

    MongoCredential credential = MongoCredential.createCredential(mongoUser, databaseName, mongoPass.toCharArray());
    ServerAddress serverAddress = new ServerAddress(mongoHost, mongoPort);

    MongoClient mongoClient = new MongoClient(serverAddress, singletonList(credential));

    return new SimpleMongoDbFactory(mongoClient, databaseName);
  }

  @Bean
  public MongoTemplate mongoTemplate(@Value("${mongo.user.name}") String mongoUser,
                                     @Value("${mongo.user.password}") String mongoPass,
                                     @Value("${mongo.database.name}") String databaseName,
                                     @Value("${mongo.host.name}") String mongoHost,
                                     @Value("${mongo.host.port}") Integer mongoPort) throws Exception {
    return new MongoTemplate(mongoDbFactory(mongoUser, mongoPass, databaseName, mongoHost, mongoPort));
  }
}
