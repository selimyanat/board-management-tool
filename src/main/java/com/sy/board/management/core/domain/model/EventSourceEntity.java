package com.sy.board.management.core.domain.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class EventSourceEntity {

  private List<DomainEvent> committedChanges;

  private List<DomainEvent> uncommittedChanges;

  protected EventSourceEntity() {

    this.committedChanges = new ArrayList<>();
    this.uncommittedChanges = new ArrayList<>();
  }

  public void markUnCommittedChangesAsCommitted() {

    this.committedChanges.addAll(this.uncommittedChanges);
    this.uncommittedChanges.removeAll(this.uncommittedChanges);
  }

  protected void apply(DomainEvent domainEvent, boolean newEvent) {

    domainEvent.rehydrate(this);

    if(newEvent)
      this.uncommittedChanges.add(domainEvent);

    if(!newEvent)
      this.committedChanges.add(domainEvent);
  }

}
