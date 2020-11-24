package com.sy.ticketingsystem.command.board.port.outgoing.adapter.repository;

import static com.sy.ticketingsystem.core.domain.model.Unit.unit;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sy.ticketingsystem.command.board.domain.model.BoardId;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardList.Status;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardListAssert;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardListCreatedEvent;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardListFixture;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardListId;
import com.sy.ticketingsystem.core.domain.model.Error;
import com.sy.ticketingsystem.core.port.outgoing.adapter.eventstore.EventStore;
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
public class
EventStoreCardListRepositoryTest {

  private static Error AN_ERROR = Error.of("Operation cannot be completed");

  private static final String CARD_LIST_NAME = "TODO";

  @Mock
  private EventStore eventStore;

  private EventStoreCardListRepository underTest;


  @BeforeEach
  public void setUp() {

    underTest = new EventStoreCardListRepository(eventStore);
  }

  @DisplayName("Calling underTest.findById(...) should return a card list by applying all past events.")
  @Test
  public void findById_returns_aCardList() {

    var boardId = BoardId.newBoardId();
    var cardListId = CardListId.newCardListId();
    var cardListCreated = CardListCreatedEvent.of(cardListId, boardId, CARD_LIST_NAME);
    when(eventStore.readFromStream(cardListId.getId()))
        .thenReturn(right(List.of(cardListCreated)));

    var result = underTest.findById(cardListId);

    assertAll(
        () -> Assertions.assertThat(result)
                        .isInstanceOf(Right.class)
                        .isNotEmpty(),
        () -> CardListAssert.assertThat(result.get().get())
                            .hasCardListId(cardListId)
                            .hasName(CARD_LIST_NAME)
                            .hasStatus(Status.ACTIVE)
                            .hasNoUncommittedChanges(),
        () -> Assertions.assertThat(result.get().get().getCommittedChanges())
                        .isNotEmpty()
                        .hasSize(1)
                        .hasOnlyElementsOfTypes(CardListCreatedEvent.class)
                        .containsExactly(cardListCreated)
    );
  }

  @DisplayName("Calling underTest.findById(...) should return an error when an error occur.")
  @Test
  public void findById_returns_error_when_an_error_occur() {

    var cardListId = CardListId.newCardListId();
    when(eventStore.readFromStream(cardListId.getId()))
        .thenReturn(left(AN_ERROR));

    var result = underTest.findById(cardListId);
    Assertions.assertThat(result)
              .isInstanceOf(Left.class)
              .isEqualTo(left(AN_ERROR));
  }

  @DisplayName("Calling underTest.getByIdOrErrorOut(...) should return an error when an error occur.")
  @Test
  public void getByIdOrErrorOut_returns_error_when_an_error_occur() {

    var cardListId = CardListId.newCardListId();
    when(eventStore.readFromStream(cardListId.getId()))
        .thenReturn(left(AN_ERROR));

    var result = underTest.getByIdOrErrorOut(cardListId);
    Assertions.assertThat(result)
              .isInstanceOf(Left.class)
              .isEqualTo(left(AN_ERROR));
  }

  @DisplayName("Calling underTest.save(...) should append all change event to the card list events.")
  @Test
  public void save_returns_cardList() {

    var boardId = BoardId.newBoardId();
    var cardList = CardListFixture.newCardList(boardId, CARD_LIST_NAME);
    var cardListCreated = cardList.getUncommittedChanges().get(0);
    when(eventStore.appendToStream(cardList.getCardListId().getId(), cardListCreated))
        .thenReturn(right(unit()));

    var result = underTest.save(cardList);
    assertAll("Card List Assert",
              () -> Assertions.assertThat(result)
                              .isInstanceOf(Right.class)
                              .isNotEmpty(),
              () -> CardListAssert.assertThat(result.get())
                                  .hasBoardId(cardList.getBoardId())
                                  .hasCardListId(cardList.getCardListId())
                                  .hasName(cardList.getName())
                                  .hasStatus(Status.ACTIVE)
                                  .hasNoUncommittedChanges(),
              () -> Assertions.assertThat(result.get().getCommittedChanges())
                              .isNotEmpty()
                              .hasSize(1)
                              .hasOnlyElementsOfTypes(CardListCreatedEvent.class)
                              .containsExactly(cardListCreated)
    );
    verify(eventStore).appendToStream(cardList.getCardListId().getId(), cardListCreated);
  }

  @DisplayName("Calling underTest.save(...) should return an error when first change cannot "
      + "be appended")
  @Test
  public void save_returns_error_when_failing_to_appendFirstEvent() {

    var boardId = BoardId.newBoardId();
    var cardList = CardListFixture.newCardList(boardId, CARD_LIST_NAME);
    var cardListCreated = cardList.getUncommittedChanges().get(0);
    when(eventStore.appendToStream(cardList.getCardListId().getId(), cardListCreated))
        .thenReturn(left(AN_ERROR));

    var result = underTest.save(cardList);
    assertAll("Save Result and Card List Assert",
              () -> Assertions.assertThat(result)
                              .isInstanceOf(Left.class)
                              .isEqualTo(left(AN_ERROR)),
              () -> Assertions.assertThat(cardList.getUncommittedChanges())
                              .isNotEmpty()
                              .containsExactly(cardListCreated),
              () -> Assertions.assertThat(cardList.getCommittedChanges())
                              .isEmpty()
    );
  }

}