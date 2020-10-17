package com.sy.ticketingsystem.core.domain.model;

import java.time.Instant;
import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(of = "id")
@ToString
public abstract class DomainEvent <T> {

  private final UUID id;

  private final Instant occurredOn;

  protected DomainEvent(UUID id, Instant occurredOn) {

    this.id = id;
    this.occurredOn = occurredOn;
  }

  protected DomainEvent() {

    this(UUID.randomUUID(), Instant.now());
  }

  public abstract T rehydrate(T t);

  public abstract String getEventName();
}
