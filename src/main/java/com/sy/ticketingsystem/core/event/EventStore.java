package com.sy.ticketingsystem.core.event;


import com.sy.ticketingsystem.core.domain.model.DomainEvent;
import com.sy.ticketingsystem.core.domain.model.Error;
import com.sy.ticketingsystem.core.domain.model.Unit;
import io.vavr.collection.List;
import io.vavr.control.Either;

public interface EventStore {

  Either<Error, List<DomainEvent>> readFromStream(String streamId);

  Either<Error, Unit> appendToStream(String streamId, DomainEvent event);

}
