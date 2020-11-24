package com.sy.ticketingsystem.core.domain.model.fixture;

public final class DomainEventFixture {

  public static ADomainEvent aDomainEvent(String prop1, String prop2) {

    return ADomainEvent.of(prop1, prop2);
  }

  public static ADomainEvent aDomainEvent1() {

    return aDomainEvent("evt1-prop1", "evt1-prop2");
  }

  public static ADomainEvent aDomainEvent2() {

    return aDomainEvent("evt2-prop1", "evt2-prop2");
  }

}
