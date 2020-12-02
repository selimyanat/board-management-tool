package com.sy.board.management.bootstrap.persistence;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = {"com.sy.board.management"})
public class MongoConfig {

}
