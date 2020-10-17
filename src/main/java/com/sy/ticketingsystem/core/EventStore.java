package com.sy.ticketingsystem.core;


import io.vavr.collection.List;
import io.vavr.control.Either;
import java.util.UUID;

public interface EventStore {

  Either<Error, List<DomainEvent>> readFromStream(UUID aggregateId);

  Either<Error, Boolean> appendToStream(UUID aggregateId, int version, DomainEvent event);



  class InMemoryEventStore implements EventStore {

    @Override
    public Either<Error, List<DomainEvent>> readFromStream(UUID aggregateId) {
      return Either.right(List.empty());
    }

    @Override
    public Either<Error, Boolean> appendToStream(UUID aggregateId, int version, DomainEvent event) {
      return Either.right(Boolean.TRUE);
    }
  };

}
