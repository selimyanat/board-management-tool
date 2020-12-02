package com.sy.board.management.core.port.outgoing.adapter.eventstore;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.sy.board.management.core.domain.model.Error;
import com.sy.board.management.core.domain.model.Unit;
import com.sy.board.management.core.domain.model.fixture.ADomainEvent;
import com.sy.board.management.core.domain.model.fixture.DomainEventFixture;
import io.vavr.collection.List;
import io.vavr.control.Either.Left;
import io.vavr.control.Either.Right;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class MongoDBEventStoreTest {

  private static final String STREAM_ID_1 = "streamid-1";

  private static final ADomainEvent A_DOMAIN_EVENT_1 = DomainEventFixture.aDomainEvent1();

  private static final ADomainEvent A_DOMAIN_EVENT_2 = DomainEventFixture.aDomainEvent2();

  private static final StoredEvent A_STORED_EVENT_1 = StoredEventFixture.of(STREAM_ID_1,
                                                                            A_DOMAIN_EVENT_1);

  private static final StoredEvent A_STORED_EVENT_2 = StoredEventFixture.of(STREAM_ID_1,
                                                                            A_DOMAIN_EVENT_2);

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

    var storedEvents = List.of(A_STORED_EVENT_1, A_STORED_EVENT_2);
    when(storedEventRepository.findByStreamId(STREAM_ID_1)).thenReturn(storedEvents);

    var result = underTest.readFromStream(STREAM_ID_1);
    assertAll(
        () -> Assertions.assertThat(result)
                        .isNotEmpty()
                        .isInstanceOf(Right.class),
        () -> Assertions.assertThat(result.get())
                        .containsExactly(A_DOMAIN_EVENT_1, A_DOMAIN_EVENT_2)
    );
  }

  @Test
  @DisplayName("Calling underTest.readFromStream(...) should return error when an "
      + "event cannot be deserialized")
  public void readFromStream_returns_error_when_an_event_cannot_be_deserialized() {

    var storedEvents = List.of(StoredEventFixture.of(STREAM_ID_1,
                                            "INVALID EVENT TYPE THAT BREAK DESERIALIZATION",
                                            A_DOMAIN_EVENT_1));
    when(storedEventRepository.findByStreamId(STREAM_ID_1)).thenReturn(storedEvents);

    var result = underTest.readFromStream(STREAM_ID_1);
    Assertions.assertThat(result.isLeft()).isTrue();
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
    Assertions.assertThat(result)
              .isInstanceOf(Left.class)
              .isEqualTo(left(Error.of(exception)));
  }

  @DisplayName("Calling underTest.appendToStream(...) should append an event to a stream.")
  @Test
  public void appendToStream_appends_domainEvents() {

    when(storedEventRepository.save(any(StoredEvent.class))).thenReturn(A_STORED_EVENT_1);

    var result = underTest.appendToStream(STREAM_ID_1, A_DOMAIN_EVENT_1);
    Assertions.assertThat(result)
              .isInstanceOf(Right.class)
              .isEqualTo(right(Unit.unit()));
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
    Assertions.assertThat(result)
              .isInstanceOf(Left.class)
              .isEqualTo(left(Error.of(exception)));
  }
}