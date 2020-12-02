package com.sy.board.management.core.query.projection;

import com.sy.board.management.core.domain.model.DomainEvent;
import org.springframework.context.event.EventListener;

public interface Projection<T extends DomainEvent> {

  @EventListener
  void on(T t);

}
