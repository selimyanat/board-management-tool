package com.sy.ticketingsystem.core.query.projection;

import com.sy.ticketingsystem.core.domain.model.DomainEvent;
import org.springframework.context.event.EventListener;

public interface Projection<T extends DomainEvent> {

  @EventListener
  void on(T t);

}
