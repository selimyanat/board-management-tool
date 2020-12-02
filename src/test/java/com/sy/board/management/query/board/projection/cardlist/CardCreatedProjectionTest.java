package com.sy.board.management.query.board.projection.cardlist;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sy.board.management.command.board.domain.model.BoardId;
import com.sy.board.management.command.board.domain.model.cardlist.CardCreatedEvent;
import com.sy.board.management.command.board.domain.model.cardlist.CardId;
import com.sy.board.management.command.board.domain.model.cardlist.CardListId;
import com.sy.board.management.query.board.domain.model.BoardView;
import com.sy.board.management.query.board.domain.model.BoardView.Status;
import com.sy.board.management.query.board.domain.model.BoardViewAssert;
import com.sy.board.management.query.board.domain.model.BoardViewFixture;
import com.sy.board.management.query.board.domain.model.CardList;
import com.sy.board.management.query.board.domain.model.CardListAssert;
import com.sy.board.management.query.board.port.outgoing.adapter.BoardViewRepository;
import java.util.ArrayList;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CardCreatedProjectionTest {

  private static final BoardId BOARD_ID = BoardId.newBoardId();

  private static final String BOARD_NAME = "BOARD_NAME";

  private static final CardListId CARD_LIST_ID = CardListId.newCardListId();

  private static final CardId CARD_ID = CardId.newCardId();

  private static final CardCreatedEvent CARD_CREATED_EVENT = CardCreatedEvent.of(
      CARD_ID,
      CARD_LIST_ID,
      BOARD_ID,
      "ADD TODO",
      "TODO DESCRIPTION"
  );

  @Mock
  private BoardViewRepository boardViewRepository;

  private CardCreatedProjection underTest;


  @BeforeEach
  public void setUp() {

    underTest = new CardCreatedProjection(boardViewRepository);
  }

  @DisplayName("On receiving a card created event, card list is updated with that card.")
  @Test
  public void on_cardCreated_cardList_asUpdated_withNewCard() {

    var cardList = CardList.of(CARD_LIST_ID.getId(), "TOOD LIST", CardList.Status.ACTIVE);
    var boardView = BoardViewFixture.of(BOARD_ID, BOARD_NAME, Status.ACTIVE, cardList);
    when(boardViewRepository.findById(BOARD_ID.getId()))
        .thenReturn(Optional.of(boardView));

    underTest.on(CARD_CREATED_EVENT);
    Assertions.assertAll(
        () -> BoardViewAssert.assertThat(boardView)
                             .hasId(BOARD_ID.getId())
                             .hasName(BOARD_NAME)
                             .hasStatus(Status.ACTIVE)
                             .hasOnlyCardLists(cardList),
        () -> verify(boardViewRepository).save(boardView)
    );
    var expectedCard = cardList.getCards().get(0);
    CardListAssert.assertThat(cardList)
                  .hasOnlyCards(expectedCard);
  }



  @DisplayName("On receiving a card created event, for a board view that does not exit the "
      + "error is logged.")
  @Test
  public void on_cardCreated_when_boardView_does_not_exist_logError() {

    when(boardViewRepository.findById(BOARD_ID.getId()))
        .thenReturn(Optional.empty());

    underTest.on(CARD_CREATED_EVENT);
    verify(boardViewRepository, never()).save(any());
  }

  @DisplayName("On receiving a card created event, for a card list that does not exit the "
      + "error is logged.")
  @Test
  public void on_cardCreated_when_cardList_does_not_exist_logError(@Mock BoardView boardView) {

    when(boardViewRepository.findById(BOARD_ID.getId()))
        .thenReturn(Optional.of(boardView));
    when(boardView.getCardLists())
        .thenReturn(new ArrayList<>());

    underTest.on(CARD_CREATED_EVENT);
    verify(boardViewRepository, never()).save(any());
  }

}