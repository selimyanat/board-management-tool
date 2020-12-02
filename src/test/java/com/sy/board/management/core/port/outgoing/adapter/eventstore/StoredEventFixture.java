package com.sy.board.management.core.port.outgoing.adapter.eventstore;

import com.sy.board.management.core.domain.model.DomainEvent;

public final class StoredEventFixture {

  private static final EventSerializer EVENT_SERIALIZER = new EventSerializer();

  public static StoredEvent of(String streamId, DomainEvent domainEvent) {

    return StoredEvent.of(streamId,
                          domainEvent.getClass().getName(),
                          EVENT_SERIALIZER.serialize(domainEvent).get());
  }

  public static StoredEvent of(String streamId, String eventType, DomainEvent domainEvent) {

    return StoredEvent.of(streamId,
                          eventType,
                          EVENT_SERIALIZER.serialize(domainEvent).get());
  }


}
