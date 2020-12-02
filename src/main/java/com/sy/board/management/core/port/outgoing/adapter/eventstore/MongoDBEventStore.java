package com.sy.board.management.core.port.outgoing.adapter.eventstore;

import static com.sy.board.management.core.domain.model.Unit.unit;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

import com.sy.board.management.core.domain.model.Error;
import com.sy.board.management.core.domain.model.DomainEvent;
import com.sy.board.management.core.domain.model.Unit;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Try;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class MongoDBEventStore implements EventStore {

  private final EventSerializer eventSerializer;

  private final StoredEventRepository storedEventRepository;

  @Override
  @SuppressWarnings("unchecked")
  public Either<Error, List<DomainEvent>> readFromStream(String streamId) {

    var result = (List<Either<Error, DomainEvent>>) Try
        .of(() -> storedEventRepository.findByStreamId(streamId)
                                       .map(this::toDomainEvent))
        .fold(throwable -> List.of(left(Error.of(throwable))),
              listOofEither -> listOofEither);

    var findError = result.find(domainEvents -> domainEvents.isLeft());
    return findError.isEmpty() ?
        right(result.map(eitherOfDomainEvent -> eitherOfDomainEvent.get())) :
        left(findError.get().getLeft());
  }

  @Override
  public Either<Error, Unit> appendToStream(String streamId, DomainEvent event) {

    return eventSerializer.serialize(event)
                          .map(json -> StoredEvent.of(streamId,
                                                      event.getClass().getName(),
                                                      json))
                          .map(storedEvent -> Try.of(() -> storedEventRepository.save(storedEvent)))
                          .map(result -> result.toEither())
                          .flatMap(result -> result)
                          .fold(throwable -> left(Error.of(throwable)),
                                storedEvent -> right(unit()));
  }

  @SuppressWarnings("unchecked")
  private <T extends DomainEvent> Either<Error, T> toDomainEvent(StoredEvent aStoredEvent) {

    return Try.of(() -> (Class<T>) Class.forName(aStoredEvent.getEventType()))
                                 .map(domainEventClass -> eventSerializer.deserialize(aStoredEvent.getPayload(), domainEventClass))
                                 .fold(throwable -> left(Error.of(throwable)),
                                       result ->  right(result.get()));
  }
}
