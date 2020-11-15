package com.sy.ticketingsystem.command.port.outgoing.adapter.repository;

import static com.sy.ticketingsystem.core.domain.model.Unit.unit;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sy.ticketingsystem.command.board.domain.model.Board.Status;
import com.sy.ticketingsystem.command.board.domain.model.BoardArchived;
import com.sy.ticketingsystem.command.board.domain.model.BoardCreated;
import com.sy.ticketingsystem.command.board.domain.model.BoardFixture;
import com.sy.ticketingsystem.command.board.domain.model.BoardId;
import com.sy.ticketingsystem.command.board.domain.model.BoardNameUpdated;
import com.sy.ticketingsystem.command.board.port.outgoing.adapter.repository.EventStoreBoardRepository;
import com.sy.ticketingsystem.core.domain.model.Error;
import com.sy.ticketingsystem.core.port.outgoing.adapter.eventstore.EventStore;
import io.vavr.collection.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class EventStoreBoardRepositoryTest {

  private static final String BOARD_NAME = "BOARD";

  @Mock
  private EventStore eventStore;

  private EventStoreBoardRepository underTest;


  @BeforeEach
  public void setUp() {

    underTest = new EventStoreBoardRepository(eventStore);
  }

  @DisplayName("Calling underTest.findById(...) should return a board by applying all past events.")
  @Test
  public void findById_returns_aBoard() {

    var boardId = BoardId.newBoardId();
    var boardCreated = BoardCreated.newInstance(boardId, BOARD_NAME);
    var boardNameUpdated = BoardNameUpdated.newInstance(BOARD_NAME,"MY_BOARD");
    var boardArchived = BoardArchived.newInstance();
    when(eventStore.readFromStream(boardId.getId())).thenReturn(right(List.of(boardCreated,
                                                                              boardNameUpdated,
                                                                              boardArchived)));

    var result = underTest.findById(boardId);
    assertAll(
        () -> assertTrue(result.isRight()),
        () -> assertFalse(result.isEmpty()),
        () -> assertEquals(boardId, result.get().get().getBoardId()),
        () -> assertEquals("MY_BOARD", result.get().get().getName()),
        () -> assertEquals(Status.ARCHIVED, result.get().get().getStatus()),
        () -> assertTrue(result.get().get().getUncommittedChanges().isEmpty()),
        () -> assertEquals(3, result.get().get().getCommittedChanges().length()),
        () -> assertEquals(boardCreated, result.get().get().getCommittedChanges().get(0)),
        () -> assertEquals(boardNameUpdated, result.get().get().getCommittedChanges().get(1)),
        () -> assertEquals(boardArchived, result.get().get().getCommittedChanges().get(2))
    );
  }

  @DisplayName("Calling underTest.save(...) should append all change event to the board events.")
  @Test
  public void save_returns_unit() {

    var board = BoardFixture.archived(BOARD_NAME);
    var boardCreated = board.getUncommittedChanges().get(0);
    var boardArchived = board.getUncommittedChanges().get(1);
    when(eventStore.appendToStream(board.getBoardId().getId(), boardCreated)).thenReturn(right(unit()));
    when(eventStore.appendToStream(board.getBoardId().getId(), boardArchived)).thenReturn(right(unit()));

    var result = underTest.save(board);
    assertAll(
        () -> assertTrue(result.isRight()),
        () -> assertTrue(board.getUncommittedChanges().isEmpty()),
        () -> assertTrue(board.getCommittedChanges().contains(boardCreated)),
        () -> assertTrue(board.getCommittedChanges().contains(boardArchived))
    );
    verify(eventStore).appendToStream(board.getBoardId().getId(), boardCreated);
    verify(eventStore).appendToStream(board.getBoardId().getId(), boardArchived);
  }


  @DisplayName("Calling boardEventStore.save(...) should return an error when first change cannot "
      + "be appended")
  @Test
  public void save_returns_error_when_failing_to_appendFirstEvent() {

    var board = BoardFixture.archived(BOARD_NAME);
    var boardCreated = board.getUncommittedChanges().get(0);
    var boardArchived = board.getUncommittedChanges().get(1);
    when(eventStore.appendToStream(board.getBoardId().getId(), boardCreated))
        .thenReturn(left(Error.of("Could not persist event")));
    when(eventStore.appendToStream(board.getBoardId().getId(), boardArchived)).thenReturn(right(unit()));

    var result = underTest.save(board);
    assertAll(
        () -> assertTrue(result.isLeft()),
        () -> assertFalse(board.getUncommittedChanges().isEmpty()),
        () -> assertTrue(board.getUncommittedChanges().contains(boardCreated)),
        () -> assertTrue(board.getUncommittedChanges().contains(boardArchived)),
        () -> assertTrue(board.getCommittedChanges().isEmpty()));
  }

  @DisplayName("Calling boardEventStore.save(...) should return an error when second change cannot "
      + "be appended")
  @Test
  public void save_returns_error_when_failing_to_appendSecondEvent() {

    var board = BoardFixture.archived(BOARD_NAME);
    var boardCreated = board.getUncommittedChanges().get(0);
    var boardArchived = board.getUncommittedChanges().get(1);
    when(eventStore.appendToStream(board.getBoardId().getId(), boardCreated))
        .thenReturn(right(unit()));
    when(eventStore.appendToStream(board.getBoardId().getId(), boardArchived))
        .thenReturn(left(Error.of("Could not persist event")));

    var result = underTest.save(board);
    assertAll(
        () -> assertTrue(result.isLeft()),
        () -> assertFalse(board.getUncommittedChanges().isEmpty()),
        () -> assertTrue(board.getUncommittedChanges().contains(boardCreated)),
        () -> assertTrue(board.getUncommittedChanges().contains(boardArchived)),
        () -> assertTrue(board.getCommittedChanges().isEmpty()));
  }
}