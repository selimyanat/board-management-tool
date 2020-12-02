package com.sy.board.management.command.board.domain.model.cardlist;

import static java.util.Objects.nonNull;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sy.board.management.command.board.domain.model.BoardId;
import com.sy.board.management.command.board.domain.model.cardlist.CardList.Status;
import com.sy.board.management.core.domain.model.DomainEvent;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class CardListTest {

  private static BoardId BOARD_ID = BoardId.newBoardId();

  private static Condition<CardList> CARD_LIST_ID_NOT_NULL = new Condition<>(
      cardList -> nonNull(cardList.getCardListId()),
      "Card List Id cannot be null"
  );

  private static Condition<Card> CARD_ID_NOT_NULL = new Condition<>(
      card -> nonNull(card.getCardId()),
      "Card id cannot be null"
  );

  private static final String CARD_LIST_NAME = "TODO_LIST";

  @DisplayName("Calling cardList.create(...) should return a card with a created event")
  @Test
  public void create_cardList_returnsCardList_with_cardListCreatedEvent() {

    var underTest = CardListFixture.newCardList(BOARD_ID, CARD_LIST_NAME);

    assertAll("Card List Assert",
              () -> CardListAssert.assertThat(underTest)
                                  .isNotNull()
                                  .has(CARD_LIST_ID_NOT_NULL)
                                  .hasBoardId(BOARD_ID)
                                  .hasName(CARD_LIST_NAME)
                                  .hasStatus(Status.ACTIVE)
                                  .hasNoCards()
                                  .hasNoCommittedChanges(),
              () -> Assertions.assertThat(underTest.getUncommittedChanges())
                              .hasSize(1)
                              .doesNotContainNull()
                              .hasOnlyElementsOfType(CardListCreatedEvent.class)
    );

    var cardListCreatedEvent = (CardListCreatedEvent) underTest.getUncommittedChanges().get(0);
    CardListCreatedEventAssert.assertThat(cardListCreatedEvent)
                              .hasBoardId(BOARD_ID)
                              .hasCardListId(underTest.getCardListId())
                              .hasName(CARD_LIST_NAME);
  }

  @DisplayName("Calling underTest.markUnCommittedChangesAsCommitted(...) moves all uncommitted "
      + "events to committed events")
  @Test
  public void markUnCommittedChangesAsCommitted_is_ok() {

    var underTest = CardListFixture.newCardList(BOARD_ID, CARD_LIST_NAME);

    underTest.markUnCommittedChangesAsCommitted();
    assertAll("Card List Assert",
              () -> CardListAssert.assertThat(underTest)
                                  .isNotNull()
                                  .has(CARD_LIST_ID_NOT_NULL)
                                  .hasBoardId(BOARD_ID)
                                  .hasName(CARD_LIST_NAME)
                                  .hasStatus(Status.ACTIVE)
                                  .hasNoCards()
                                  .hasNoUncommittedChanges(),
              () -> Assertions.assertThat(underTest.getCommittedChanges())
                              .hasSize(1)
                              .doesNotContainNull()
                              .hasOnlyElementsOfTypes(CardListCreatedEvent.class)
    );
  }

  @DisplayName("Calling CardList.fromHistory(...) returns cardList by applying all past events")
  @Test
  public void fromHistory_returns_board() {

    var aCardListId = CardListId.newCardListId();
    List<DomainEvent> history = List.of(
        CardListCreatedEvent.of(aCardListId, BOARD_ID, CARD_LIST_NAME)
    );

    var underTest = CardList.fromHistory(aCardListId, history).get();
    assertAll("Card List Assert",
              () -> CardListAssert.assertThat(underTest)
                                  .has(CARD_LIST_ID_NOT_NULL)
                                  .hasBoardId(BOARD_ID)
                                  .hasName(CARD_LIST_NAME)
                                  .hasStatus(Status.ACTIVE)
                                  .hasNoCards()
                                  .hasNoUncommittedChanges(),
              () -> Assertions.assertThat(underTest.getCommittedChanges())
                              .containsExactlyElementsOf(history)
    );
  }

  @DisplayName("Calling underTest.createCard(...) returns a new card with card created event.")
  @Test
  public void create_card_returns_card_with_cardCreatedEvent() {

    var cardName = "Enable login with MFA";
    var description = "Enable MFA flow in Authentication provider component.";
    var underTest = CardListFixture.newCardList(BOARD_ID, CARD_LIST_NAME);

    underTest.createCard(cardName, description);
    assertAll("Card List Assert",
              () -> CardListAssert.assertThat(underTest)
                                  .isNotNull()
                                  .has(CARD_LIST_ID_NOT_NULL)
                                  .hasBoardId(BOARD_ID)
                                  .hasName(CARD_LIST_NAME)
                                  .hasStatus(Status.ACTIVE)
                                  .hasNoCommittedChanges(),
              () -> Assertions.assertThat(underTest.getCards())
                              .hasSize(1)
                              .doesNotContainNull(),
              () -> Assertions.assertThat(underTest.getUncommittedChanges())
                              .hasSize(2)
                              .doesNotContainNull()
                              .hasOnlyElementsOfTypes(CardListCreatedEvent.class,
                                                      CardCreatedEvent.class));

    var card = underTest.getCards().get(0);
    CardAssert.assertThat(card)
              .has(CARD_ID_NOT_NULL)
              .hasCardListId(underTest.getCardListId())
              .hasName(cardName)
              .hasDescription(description);
  }


}