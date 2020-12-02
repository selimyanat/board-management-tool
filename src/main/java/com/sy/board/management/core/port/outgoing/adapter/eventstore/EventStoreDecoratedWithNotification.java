package com.sy.board.management.core.port.outgoing.adapter.eventstore;

import com.sy.board.management.core.domain.model.DomainEvent;
import com.sy.board.management.core.domain.model.Error;
import com.sy.board.management.core.domain.model.Unit;
import io.vavr.collection.List;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;

@AllArgsConstructor
@Slf4j
public class EventStoreDecoratedWithNotification implements EventStore {

  private final EventStore decorated;

  private final ApplicationEventPublisher publisher;

  @Override
  public Either<Error, List<DomainEvent>> readFromStream(String streamId) {

    return decorated.readFromStream(streamId);
  }

  @Override
  public Either<Error, Unit> appendToStream(String streamId, DomainEvent event) {

    var result = decorated.appendToStream(streamId, event)
                          .peek(unit -> LOG.debug("Event {} successfully appended to "
                                                      + "event store for stream id {}",
                                                  event,
                                                  streamId))
                          .peekLeft(error -> LOG.error("Could not append event {} to stream id {} because of the following:{}",
                                                       event,
                                                       streamId,
                                                       error.getMessage()));

    if (result.isRight())
      publisher.publishEvent(event);
    return result;
  }

}

