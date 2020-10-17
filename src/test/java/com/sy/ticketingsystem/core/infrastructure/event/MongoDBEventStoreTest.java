package com.sy.ticketingsystem.core.infrastructure.event;

import static com.sy.ticketingsystem.core.domain.model.Unit.unit;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.sy.ticketingsystem.core.domain.model.DomainEvent;
import com.sy.ticketingsystem.core.event.EventSerializer;
import io.vavr.collection.List;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MongoDBEventStoreTest {

  private static final String STREAM_ID_1 = "streamid-1";

  private static final ADomainEvent A_DOMAIN_EVENT_1 = ADomainEvent.newInstance("evt1-prop1", "evt1-prop2");

  private static final ADomainEvent A_DOMAIN_EVENT_2 = ADomainEvent.newInstance("evt1-prop1", "evt2-prop2");

  @Mock
  private EventSerializer eventSerializer;

  @Mock
  private StoredEventRepository storedEventRepository;

  private MongoDBEventStore underTest;

  @BeforeEach
  public void setUp() {

    eventSerializer = new EventSerializer();
    underTest = new MongoDBEventStore(eventSerializer, storedEventRepository);
  }

  @DisplayName("Calling underTest.readFromStream(...) should return all events related to"
      + " a streamid")
  @Test
  public void readFromStream_returns_domainEvents() {

    when(storedEventRepository.findByStreamId(STREAM_ID_1)).thenReturn(List.of(
        StoredEvent.newInstance(STREAM_ID_1,
                                A_DOMAIN_EVENT_1.getClass().getName(),
                                eventSerializer.serialize(A_DOMAIN_EVENT_1).get()),
        StoredEvent.newInstance(STREAM_ID_1,
                                A_DOMAIN_EVENT_2.getClass().getName(),
                                eventSerializer.serialize(A_DOMAIN_EVENT_2).get())
    ));

    var result = underTest.readFromStream(STREAM_ID_1);
    assertAll(
        () -> assertTrue(result.isRight()),
        () -> assertTrue(result.get().contains(A_DOMAIN_EVENT_1)),
        () -> assertTrue(result.get().contains(A_DOMAIN_EVENT_2))
    );
  }

  @Test
  @DisplayName("Calling underTest.readFromStream(...) should return error when an "
      + "event cannot be deserialized is raised")
  public void readFromStream_returns_error_when_an_event_cannot_be_deserialized() {

    when(storedEventRepository.findByStreamId(STREAM_ID_1)).thenReturn(List.of(
        StoredEvent.newInstance(STREAM_ID_1,
                                "INVALID EVENT TYPE THAT BREAK DESERIALIZATION",
                                eventSerializer.serialize(A_DOMAIN_EVENT_1).get())));

    var result = underTest.readFromStream(STREAM_ID_1);
    assertAll(() -> assertTrue(result.isLeft()));
  }

  @Test
  @DisplayName("Calling underTest.readFromStream(...) should return error when an "
      + "exception occur while reading from the database")
  public void readFromStream_returns_error_when_on_database_exception() {

    var exception = new RuntimeException("cannot read from database");
    doThrow(exception)
           .when(storedEventRepository)
           .findByStreamId(STREAM_ID_1);

    var result = underTest.readFromStream(STREAM_ID_1);
    assertAll(
        () -> assertTrue(result.isLeft()),
        () -> assertEquals(exception.getMessage(), result.getLeft().getMessage())
    );
  }

  @DisplayName("Calling underTest.appendToStream(...) should append an event to a stream.")
  @Test
  public void appendToStream_appends_domainEvents() {

    var storedEvt1 = StoredEvent.newInstance(STREAM_ID_1,
                                             A_DOMAIN_EVENT_1.getClass().getName(),
                                             eventSerializer.serialize(A_DOMAIN_EVENT_1).get());

    when(storedEventRepository.save(any(StoredEvent.class))).thenReturn(storedEvt1);

    var result = underTest.appendToStream(STREAM_ID_1, A_DOMAIN_EVENT_1);
    assertAll(
        () -> assertTrue(result.isRight()),
        () -> assertEquals(unit(), result.get())
    );
  }

  @DisplayName("Calling underTest.appendToStream(...) should return an error when an "
      + "exception occur while writing to the database.")
  @Test
  public void appendToStream_returns_error_on_database_exception() {

    var exception = new RuntimeException("cannot read from database");
    doThrow(exception)
        .when(storedEventRepository)
        .save(any(StoredEvent.class));


    var result = underTest.appendToStream(STREAM_ID_1, A_DOMAIN_EVENT_1);
    assertAll(
        () -> assertTrue(result.isLeft()),
        () -> assertEquals(exception.getMessage(), result.getLeft().getMessage())
    );
  }

  @AllArgsConstructor
  public static class ADomainEvent extends DomainEvent<String> {

    private final String prop1;

    private final String prop2;

    static ADomainEvent newInstance(String prop1, String prop2) {

      return new ADomainEvent(prop1, prop2);
    }

    @Override
    public String rehydrate(String s) {

      return s;
    }

    @Override
    public String getEventName() {

      return "ADomainEvent";
    }
  }
}