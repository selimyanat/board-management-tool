package com.sy.ticketingsystem.command.board.domain.model;

import static com.sy.ticketingsystem.command.board.domain.model.Board.Status.ACTIVE;
import static com.sy.ticketingsystem.command.board.domain.model.Board.Status.ARCHIVED;
import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardListAssert;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardListCreatedEvent;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardListCreatedEventAssert;
import com.sy.ticketingsystem.command.board.domain.model.fixture.BoardFixture;
import com.sy.ticketingsystem.core.domain.model.DomainEvent;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BoardTest {

  static Condition<Board> BOARD_ID_NOT_NULL = new Condition<>(board -> nonNull(board.getBoardId()),
                                                              "Board Id cannot be null");

  private static final String BOARD_NAME = "MY_BOARD";

  private static final String NEW_BOARD_NAME = "NEW_NAME";

  private static final String BOARD_CARD_LIST_NAME = "TODO_LIST";

  @DisplayName("Calling board.create(...) should return a board with a created event")
  @Test
  public void create_board_returnsBoard_with_boardCreatedEvent() {

    var underTest = BoardFixture.newBoard(BOARD_NAME);

    assertAll("Board Assert",
        () -> BoardAssert.assertThat(underTest)
                         .isNotNull()
                         .has(BOARD_ID_NOT_NULL)
                         .hasName(BOARD_NAME)
                         .hasStatus(ACTIVE)
                         .hasNoBoardCardLists()
                         .hasNoCommittedChanges(),
        () -> Assertions.assertThat(underTest.getUncommittedChanges())
                        .hasSize(1)
                        .doesNotContainNull()
                        .hasOnlyElementsOfType(BoardCreated.class)
    );

    var boardCreated = (BoardCreated) underTest.getUncommittedChanges().get(0);
    BoardCreatedAssert.assertThat(boardCreated)
                      .hasBoardId(underTest.getBoardId())
                      .hasBoardName(BOARD_NAME);
  }

  @DisplayName("Calling board.updateName(...) should return a board with a name updated event")
  @Test
  public void update_board_name_returnsBoard_with_boardNameUpdatedEvent() {

    var underTest = BoardFixture.updatedBoardName(BOARD_NAME, NEW_BOARD_NAME);

    assertAll("Board Assert",
        () -> BoardAssert.assertThat(underTest)
                         .has(BOARD_ID_NOT_NULL)
                         .hasName(NEW_BOARD_NAME)
                         .hasStatus(ACTIVE)
                         .hasNoBoardCardLists()
                         .hasNoCommittedChanges(),
        () ->  Assertions.assertThat(underTest.getUncommittedChanges())
                         .hasSize(2)
                         .doesNotContainNull()
                         .hasOnlyElementsOfTypes(BoardCreated.class, BoardNameUpdated.class)
    );

    var boardUpdated = (BoardNameUpdated) underTest.getUncommittedChanges().get(1);
    BoardNameUpdatedAssert.assertThat(boardUpdated)
                          .hasBoardId(underTest.getBoardId())
                          .hasFormerName(BOARD_NAME)
                          .hasNewName(NEW_BOARD_NAME);
  }

  @DisplayName("Calling board.archive(...) should return a board with an archived event")
  @Test
  public void archive_board_returnsBoard_boardArchivedEvent() {

    var underTest = BoardFixture.archivedBoard(BOARD_NAME);

    assertAll("Board Assert",
        () -> BoardAssert.assertThat(underTest)
                         .has(BOARD_ID_NOT_NULL)
                         .hasName(BOARD_NAME)
                         .hasStatus(ARCHIVED)
                         .hasNoBoardCardLists()
                         .hasNoCommittedChanges(),
        () -> Assertions.assertThat(underTest.getUncommittedChanges())
                        .hasSize(2)
                        .doesNotContainNull()
                        .hasOnlyElementsOfTypes(BoardCreated.class, BoardArchived.class)
    );

    var boardArchived = (BoardArchived) underTest.getUncommittedChanges().get(1);
    BoardArchivedAssert.assertThat(boardArchived)
                       .hasBoardId(underTest.getBoardId())
                       .hasFormerStatus(ACTIVE)
                       .hasStatus(ARCHIVED);
  }

  @DisplayName("Calling board.markUnCommittedChangesAsCommitted(...) moves all uncommitted events to committed events")
  @Test
  public void markUnCommittedChangesAsCommitted_is_ok() {

    var underTest = BoardFixture.archivedBoard(BOARD_NAME);

    underTest.markUnCommittedChangesAsCommitted();
    assertAll("Board Assert",
        () -> BoardAssert.assertThat(underTest)
                         .hasNoUncommittedChanges(),
        () -> Assertions.assertThat(underTest.getCommittedChanges())
                        .hasSize(2)
                        .doesNotContainNull()
                        .hasOnlyElementsOfTypes(BoardCreated.class, BoardArchived.class)
    );
  }

  @DisplayName("Calling Board.fromHistory(...) returns board by applying all past events")
  @Test
  public void fromHistory_returns_board() {

    var aBoardId = BoardId.newBoardId();
    List<DomainEvent> history = List.of(
        BoardCreated.of(aBoardId, BOARD_NAME),
        BoardNameUpdated.of(aBoardId, BOARD_NAME, NEW_BOARD_NAME),
        BoardArchived.of(aBoardId, ACTIVE)
    );

    var underTest = Board.fromHistory(aBoardId, history).get();
    assertAll("Board Assert",
        () -> BoardAssert.assertThat(underTest)
                         .hasBoardId(aBoardId)
                         .hasName(NEW_BOARD_NAME)
                         .hasStatus(ARCHIVED)
                         .hasNoBoardCardLists()
                         .hasNoUncommittedChanges(),
        () -> Assertions.assertThat(underTest.getCommittedChanges())
                        .containsExactlyElementsOf(history)
    );
  }

  @DisplayName("Calling board.addCardList(...) returns a new CardList and a board with a board "
      + "card list created event")
  @Test
  public void add_card_list_returnsBoard_with_boardCardListCreatedEvent_and_cardList() {

    var underTest = BoardFixture.newBoard(BOARD_NAME);
    var cardList = underTest.addCardList(BOARD_CARD_LIST_NAME).get();

    assertAll("Board Assert",
        () -> BoardAssert.assertThat(underTest)
                         .has(BOARD_ID_NOT_NULL)
                         .hasName(BOARD_NAME)
                         .hasStatus(ACTIVE)
                         .hasNoCommittedChanges(),
        () -> Assertions.assertThat(underTest.getBoardCardLists())
                        .hasSize(1)
                        .doesNotContainNull(),
        () -> Assertions.assertThat(underTest.getUncommittedChanges())
                        .hasSize(2)
                        .doesNotContainNull()
                        .hasOnlyElementsOfTypes(BoardCreated.class,
                                                BoardCardListCreated.class)
    );

    var boardCardList = underTest.getBoardCardLists().get(0);
    assertAll("Card List Assert",
        () -> BoardCardListAssert.assertThat(boardCardList)
                                 .hasBoardId(underTest.getBoardId())
                                 .hasCardListId(cardList.getCardListId())
                                 .hasOrdering(1),
        () -> CardListAssert.assertThat(cardList)
                            .hasBoardId(underTest.getBoardId())
                            .hasCardListId(boardCardList.getCardListId())
                            .hasName(BOARD_CARD_LIST_NAME)
                            .hasNoCommittedChanges(),
        () -> Assertions.assertThat(cardList.getUncommittedChanges())
                        .doesNotContainNull()
                        .hasSize(1)
                        .hasOnlyElementsOfTypes(CardListCreatedEvent.class)

    );

    var cardListCreatedEvent = (CardListCreatedEvent) cardList.getUncommittedChanges().get(0);
    assertAll("Card List Event Assert",
        () -> CardListCreatedEventAssert.assertThat(cardListCreatedEvent)
                                        .hasBoardId(underTest.getBoardId())
                                        .hasCardListId(cardList.getCardListId())
                                        .hasName(cardList.getName())
    );
  }
}
