package com.sy.board.management.core.port.outgoing.adapter.eventstore;


import com.sy.board.management.core.domain.model.DomainEvent;
import com.sy.board.management.core.domain.model.Error;
import com.sy.board.management.core.domain.model.Unit;
import io.vavr.collection.List;
import io.vavr.control.Either;

public interface EventStore {

  Either<Error, List<DomainEvent>> readFromStream(String streamId);

  Either<Error, Unit> appendToStream(String streamId, DomainEvent event);

}
