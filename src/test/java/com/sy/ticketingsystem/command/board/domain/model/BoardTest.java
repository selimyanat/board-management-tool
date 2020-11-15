package com.sy.ticketingsystem.command.board.domain.model;

import static com.sy.ticketingsystem.command.board.domain.model.Board.Status.ACTIVE;
import static com.sy.ticketingsystem.command.board.domain.model.Board.Status.ARCHIVED;
import static com.sy.ticketingsystem.command.board.domain.model.BoardArchived.BOARD_ARCHIVED;
import static com.sy.ticketingsystem.command.board.domain.model.BoardCreated.BOARD_CREATED;
import static com.sy.ticketingsystem.command.board.domain.model.BoardNameUpdated.BOARD_NAME_UPDATED;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sy.ticketingsystem.core.domain.model.DomainEvent;
import io.vavr.collection.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BoardTest {

  private static final String BOARD_NAME = "MY_BOARD";

  @DisplayName("Calling board.create(...) should return a board with a created event")
  @Test
  public void create_board_returnsBoard_with_boardCreatedEvent() {

    var state = BoardFixture.newBoard(BOARD_NAME);

    assertAll(
        () -> assertNotNull(state.getBoardId()),
        () -> assertEquals(BOARD_NAME, state.getName()),
        () -> assertEquals(ACTIVE, state.getStatus()),
        () -> assertEquals(1, state.getUncommittedChanges().length()),
        () -> assertEquals(BOARD_CREATED, state.getUncommittedChanges().get(0).getEventName()),
        () -> assertTrue(state.getCommittedChanges().isEmpty())
    );
  }

  @DisplayName("Calling board.updateName(...) should return a board with a name updated event")
  @Test
  public void update_board_name_returnsBoard_with_boardNameUpdatedEvent() {

    var state = BoardFixture.newBoard(BOARD_NAME);
    var newState = state.updateName("NEW_NAME").get();

    assertAll(
        () -> assertEquals(state.getBoardId(), newState.getBoardId()),
        () -> assertEquals("NEW_NAME", newState.getName()),
        () -> assertEquals(state.getStatus(), newState.getStatus()),
        () -> assertEquals(2, newState.getUncommittedChanges().length()),
        () -> assertEquals(BOARD_CREATED, newState.getUncommittedChanges().get(0).getEventName()),
        () -> assertEquals(BOARD_NAME_UPDATED, newState.getUncommittedChanges().get(1).getEventName()),
        () -> assertTrue(newState.getCommittedChanges().isEmpty())
    );
  }

  @DisplayName("Calling board.archive(...) should return a board with an archived event")
  @Test
  public void archive_board_returns_boardArchivedEvent() {

    var state = BoardFixture.newBoard(BOARD_NAME);
    var newState = state.archive().get();

    assertAll(
        () -> assertEquals(state.getBoardId(), newState.getBoardId()),
        () -> assertEquals(state.getName(), newState.getName()),
        () -> assertEquals(ARCHIVED, newState.getStatus()),
        () -> assertEquals(2, newState.getUncommittedChanges().length()),
        () -> assertEquals(BOARD_CREATED, newState.getUncommittedChanges().get(0).getEventName()),
        () -> assertEquals(BOARD_ARCHIVED, newState.getUncommittedChanges().get(1).getEventName()),
        () -> assertTrue(newState.getCommittedChanges().isEmpty())
    );
  }

  @DisplayName("Calling board.markUnCommittedChangesAsCommitted(...) moves all uncommitted events to committed events")
  @Test
  public void markUnCommittedChangesAsCommitted_is_ok() {

    var state = BoardFixture.archived(BOARD_NAME);
    state.markUnCommittedChangesAsCommitted();

    assertAll(
        () -> assertTrue(state.getUncommittedChanges().isEmpty()),
        () -> assertEquals(BOARD_CREATED, state.getCommittedChanges().get(0).getEventName()),
        () -> assertEquals(BOARD_ARCHIVED, state.getCommittedChanges().get(1).getEventName())
    );
  }

  @DisplayName("Calling Board.fromHistory(...) returns board by applying all past events")
  @Test
  public void fromHistory_returns_board() {

    var aBoardId = BoardId.newBoardId();
    List<DomainEvent> history = List.of(
        BoardCreated.newInstance(aBoardId, BOARD_NAME),
        BoardNameUpdated.newInstance(BOARD_NAME, "NEW_NAME"),
        BoardArchived.newInstance()
    );

    var state = Board.fromHistory(aBoardId, history).get();
    assertAll(
        () -> assertEquals(aBoardId, state.getBoardId()),
        () -> assertEquals("NEW_NAME", state.getName()),
        () -> assertEquals(ARCHIVED, state.getStatus()),
        () -> assertEquals(3, state.getCommittedChanges().length()),
        () -> assertEquals(BOARD_CREATED, state.getCommittedChanges().get(0).getEventName()),
        () -> assertEquals(BOARD_NAME_UPDATED, state.getCommittedChanges().get(1).getEventName()),
        () -> assertEquals(BOARD_ARCHIVED, state.getCommittedChanges().get(2).getEventName()),
        () -> assertTrue(state.getUncommittedChanges().isEmpty())
    );
  }
}
