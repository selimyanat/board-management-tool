package com.sy.board.management.bootstrap.persistence;

import com.sy.board.management.core.port.outgoing.adapter.eventstore.EventSerializer;
import com.sy.board.management.core.port.outgoing.adapter.eventstore.EventStore;
import com.sy.board.management.core.port.outgoing.adapter.eventstore.EventStoreDecoratedWithNotification;
import com.sy.board.management.core.port.outgoing.adapter.eventstore.MongoDBEventStore;
import com.sy.board.management.core.port.outgoing.adapter.eventstore.StoredEventRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventStoreConfig {

  @Bean
  EventStore eventStore(EventSerializer eventSerializer,
                        StoredEventRepository storedEventRepository,
                        ApplicationEventPublisher applicationEventPublisher) {

    var mongoEventStore = new MongoDBEventStore(eventSerializer, storedEventRepository);
    return new EventStoreDecoratedWithNotification(mongoEventStore, applicationEventPublisher);
  }


}
