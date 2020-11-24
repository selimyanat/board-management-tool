package com.sy.ticketingsystem.core.domain.model;

import io.vavr.collection.List;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class EventSourceEntity {

  private List<DomainEvent> committedChanges;

  private List<DomainEvent> uncommittedChanges;

  public void markUnCommittedChangesAsCommitted() {

    this.committedChanges = this.committedChanges.appendAll(this.uncommittedChanges);
    this.uncommittedChanges = this.uncommittedChanges.removeAll(this.uncommittedChanges);
  }

  protected void apply(DomainEvent domainEvent, boolean newEvent) {

    domainEvent.rehydrate(this);

    if(newEvent)
      this.uncommittedChanges = this.uncommittedChanges.append(domainEvent);

    if(!newEvent)
      this.committedChanges = this.committedChanges.append(domainEvent);
  }

}
