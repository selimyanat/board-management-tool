package com.sy.ticketingsystem.core.domain.model.fixture;

import com.sy.ticketingsystem.core.domain.model.DomainEvent;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class ADomainEvent extends DomainEvent<String> {

  private final String prop1;

  private final String prop2;

  @Override
  public String rehydrate(String s) {

    return s;
  }

  @Override
  public String getEventName() {

    return "ADomainEvent";
  }
}
