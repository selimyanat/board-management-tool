package com.sy.board.management.core.port.outgoing.adapter.eventstore;

import java.util.UUID;
import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stored_events")
@CompoundIndex(def = "{'streamId':1}", name = "stored_events_stream-id_version_compound_index")
@Value(staticConstructor = "of")
public class StoredEvent {

  @Id
  private UUID id;

  private String streamId;

  // TODO put it back
  //@Version
  //private Integer streamVersion;

  private String eventType;

  private String payload;

  public static StoredEvent of(String streamId,
                               String eventType,
                               String payload) {

    return new StoredEvent(UUID.randomUUID(), streamId, eventType, payload);
  }
}