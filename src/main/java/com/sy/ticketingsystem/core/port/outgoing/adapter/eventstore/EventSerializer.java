package com.sy.ticketingsystem.core.port.outgoing.adapter.eventstore;

import com.fatboyindustrial.gsonjavatime.Converters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sy.ticketingsystem.core.domain.model.DomainEvent;
import io.vavr.control.Either;
import io.vavr.control.Try;
import org.springframework.stereotype.Component;

@Component
public class EventSerializer {

  private Gson gson;

  public EventSerializer() {

    this.gson = Converters.registerOffsetDateTime(new GsonBuilder()).create();
  }

  public Either<Throwable, String> serialize(DomainEvent aDomainEvent) {

    return Try.of(() -> gson.toJson(aDomainEvent)).toEither();
  }

  public <T extends DomainEvent> Either<Throwable, T> deserialize(String aPayload,
                                                                   Class<T> aType) {

    return Try.of(() -> gson.fromJson(aPayload, aType)).toEither();
  }


}
