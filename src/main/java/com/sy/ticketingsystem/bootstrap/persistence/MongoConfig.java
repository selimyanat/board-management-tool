package com.sy.ticketingsystem.bootstrap.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = {"com.sy.ticketingsystem"})
public class MongoConfig {

}
