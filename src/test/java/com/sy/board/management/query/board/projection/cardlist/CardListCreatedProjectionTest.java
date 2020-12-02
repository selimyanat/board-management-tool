package com.sy.board.management.query.board.projection.cardlist;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sy.board.management.command.board.domain.model.BoardId;
import com.sy.board.management.command.board.domain.model.cardlist.CardListCreatedEvent;
import com.sy.board.management.command.board.domain.model.cardlist.CardListId;
import com.sy.board.management.query.board.domain.model.BoardView.Status;
import com.sy.board.management.query.board.domain.model.BoardViewAssert;
import com.sy.board.management.query.board.domain.model.BoardViewFixture;
import com.sy.board.management.query.board.domain.model.CardList;
import com.sy.board.management.query.board.port.outgoing.adapter.BoardViewRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CardListCreatedProjectionTest {

  private static final BoardId BOARD_ID = BoardId.newBoardId();

  private static final String BOARD_NAME = "BOARD_NAME";

  private static final CardListCreatedEvent CARD_LIST_CREATED_EVENT = CardListCreatedEvent.of(
      CardListId.newCardListId(),
      BOARD_ID,
      "TODO_LIST"
  );

  @Mock
  private BoardViewRepository boardViewRepository;

  private CardListCreatedProjection underTest;

  @BeforeEach
  public void setUp() {

    underTest = new CardListCreatedProjection(boardViewRepository);
  }

  @DisplayName("On receiving a card list created event, a board view is updated with that card "
      + "list.")
  @Test
  public void on_cardListCreated_boardView_isUpdated_withCardList() {

    var boardView = BoardViewFixture.of(BOARD_ID, BOARD_NAME, Status.ACTIVE);
    when(boardViewRepository.findById(CARD_LIST_CREATED_EVENT.getBoardId().getId()))
        .thenReturn(Optional.of(boardView));

    underTest.on(CARD_LIST_CREATED_EVENT);
    Assertions.assertAll(
        () -> BoardViewAssert.assertThat(boardView)
                             .hasId(BOARD_ID.getId())
                             .hasName(BOARD_NAME)
                             .hasStatus(Status.ACTIVE)
                             .hasOnlyCardLists(
                                 CardList.of(CARD_LIST_CREATED_EVENT.getCardListId().getId(),
                                             CARD_LIST_CREATED_EVENT.getName(),
                                             CardList.Status.ACTIVE)),
        () -> verify(boardViewRepository).save(boardView)
    );
  }

  @DisplayName("On receiving a card list created event, for a board view that does not exit the "
      + "error is logged.")
  @Test
  public void on_cardListCreated_when_boardView_does_not_exist_logError() {

    when(boardViewRepository.findById(BOARD_ID.getId()))
        .thenReturn(Optional.empty());

    underTest.on(CARD_LIST_CREATED_EVENT);
    verify(boardViewRepository, never()).save(any());
  }



}