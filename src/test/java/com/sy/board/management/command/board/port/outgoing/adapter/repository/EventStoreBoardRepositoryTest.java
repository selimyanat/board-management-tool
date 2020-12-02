package com.sy.board.management.command.board.port.outgoing.adapter.repository;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sy.board.management.command.board.domain.model.Board.Status;
import com.sy.board.management.command.board.domain.model.BoardArchived;
import com.sy.board.management.command.board.domain.model.BoardAssert;
import com.sy.board.management.command.board.domain.model.BoardCreated;
import com.sy.board.management.command.board.domain.model.BoardId;
import com.sy.board.management.command.board.domain.model.BoardNameUpdated;
import com.sy.board.management.command.board.domain.model.fixture.BoardFixture;
import com.sy.board.management.core.domain.model.Error;
import com.sy.board.management.core.domain.model.Unit;
import com.sy.board.management.core.port.outgoing.adapter.eventstore.EventStore;
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
public class EventStoreBoardRepositoryTest {

  private static Error AN_ERROR = Error.of("Operation cannot be completed");

  private static final String BOARD_NAME = "BOARD";

  private static final String NEW_BOARD_NAME = "MY_BOARD";

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
    var boardCreated = BoardCreated.of(boardId, BOARD_NAME);
    var boardNameUpdated = BoardNameUpdated.of(boardId, BOARD_NAME, NEW_BOARD_NAME);
    var boardArchived = BoardArchived.of(boardId, Status.ACTIVE);
    when(eventStore.readFromStream(boardId.getId())).thenReturn(right(List.of(boardCreated,
                                                                              boardNameUpdated,
                                                                              boardArchived)));

    var result = underTest.findById(boardId);

    assertAll(
        () -> Assertions.assertThat(result)
                        .isInstanceOf(Right.class)
                        .isNotEmpty(),
        () -> BoardAssert.assertThat(result.get().get())
                         .hasBoardId(boardId)
                         .hasName(NEW_BOARD_NAME)
                         .hasStatus(Status.ARCHIVED)
                         .hasNoBoardCardLists()
                         .hasNoUncommittedChanges(),
        () -> Assertions.assertThat(result.get().get().getCommittedChanges())
                        .isNotEmpty()
                        .hasSize(3)
                        .hasOnlyElementsOfTypes(BoardCreated.class,
                                                BoardNameUpdated.class,
                                                BoardArchived.class)
                        .containsExactly(boardCreated, boardNameUpdated, boardArchived)
    );
  }

  @DisplayName("Calling underTest.findById(...) should return an error when an error occur.")
  @Test
  public void findById_returns_error_when_an_error_occur() {

    var boardId = BoardId.newBoardId();
    when(eventStore.readFromStream(boardId.getId()))
        .thenReturn(left(AN_ERROR));

    var result = underTest.findById(boardId);
    Assertions.assertThat(result)
              .isInstanceOf(Left.class)
              .isEqualTo(left(AN_ERROR));
  }

  @DisplayName("Calling underTest.findByIdOrErrorOut(...) should return an error when an error "
      + "occur.")
  @Test
  public void getById_returns_error_when_an_error_occur() {

    var boardId = BoardId.newBoardId();
    when(eventStore.readFromStream(boardId.getId()))
        .thenReturn(left(AN_ERROR));

    var result = underTest.getByIdOrErrorOut(boardId);
    Assertions.assertThat(result)
              .isInstanceOf(Left.class)
              .isEqualTo(left(AN_ERROR));
  }

  @DisplayName("Calling underTest.save(...) should append all change event to the board events.")
  @Test
  public void save_returns_board() {

    var board = BoardFixture.archivedBoard(BOARD_NAME);
    var boardCreated = board.getUncommittedChanges().get(0);
    var boardArchived = board.getUncommittedChanges().get(1);
    when(eventStore.appendToStream(board.getBoardId().getId(), boardCreated))
        .thenReturn(right(Unit.unit()));
    when(eventStore.appendToStream(board.getBoardId().getId(), boardArchived))
        .thenReturn(right(Unit.unit()));

    var result = underTest.save(board);
    assertAll("Board Assert",
        () -> Assertions.assertThat(result)
                        .isInstanceOf(Right.class)
                        .isNotEmpty(),
        () -> BoardAssert.assertThat(result.get())
                         .hasBoardId(board.getBoardId())
                         .hasName(board.getName())
                         .hasStatus(Status.ARCHIVED)
                         .hasNoBoardCardLists()
                         .hasNoUncommittedChanges(),
        () -> Assertions.assertThat(result.get().getCommittedChanges())
                        .isNotEmpty()
                        .hasSize(2)
                        .hasOnlyElementsOfTypes(BoardCreated.class, BoardArchived.class)
                        .containsExactly(boardCreated, boardArchived)
    );
    verify(eventStore).appendToStream(board.getBoardId().getId(), boardCreated);
    verify(eventStore).appendToStream(board.getBoardId().getId(), boardArchived);
  }


  @DisplayName("Calling underTest.save(...) should return an error when first change cannot "
      + "be appended")
  @Test
  public void save_returns_error_when_failing_to_appendFirstEvent() {

    var board = BoardFixture.archivedBoard(BOARD_NAME);
    var boardCreated = board.getUncommittedChanges().get(0);
    var boardArchived = board.getUncommittedChanges().get(1);
    when(eventStore.appendToStream(board.getBoardId().getId(), boardCreated)).thenReturn(left(AN_ERROR));

    var result = underTest.save(board);
    assertAll("Save Result and Board Assert",
        () -> Assertions.assertThat(result)
                        .isInstanceOf(Left.class)
                        .isEqualTo(left(AN_ERROR)),
        () -> Assertions.assertThat(board.getUncommittedChanges())
                        .isNotEmpty()
                        .containsExactly(boardCreated, boardArchived),
        () -> Assertions.assertThat(board.getCommittedChanges())
                        .isEmpty()
    );
  }

  @DisplayName("Calling underTest.save(...) should return an error when second change cannot "
      + "be appended")
  @Test
  public void save_returns_error_when_failing_to_appendSecondEvent() {

    var board = BoardFixture.archivedBoard(BOARD_NAME);
    var boardCreated = board.getUncommittedChanges().get(0);
    var boardArchived = board.getUncommittedChanges().get(1);
    when(eventStore.appendToStream(board.getBoardId().getId(), boardCreated))
        .thenReturn(right(Unit.unit()));
    when(eventStore.appendToStream(board.getBoardId().getId(), boardArchived)).thenReturn(left(AN_ERROR));

    var result = underTest.save(board);
    assertAll("Save Result and Board Assert",
              () -> Assertions.assertThat(result)
                              .isInstanceOf(Left.class)
                              .isEqualTo(left(AN_ERROR)),
              () -> Assertions.assertThat(board.getUncommittedChanges())
                              .isNotEmpty()
                              .containsExactly(boardCreated, boardArchived),
              () -> Assertions.assertThat(board.getCommittedChanges())
                              .isEmpty()
    );
  }
}