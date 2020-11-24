package com.sy.ticketingsystem.command.board.usecase;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.sy.ticketingsystem.command.board.domain.model.Board;
import com.sy.ticketingsystem.command.board.domain.model.BoardId;
import com.sy.ticketingsystem.command.board.domain.model.BoardRepository;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.Card;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardAssert;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardId;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardList;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardListFixture;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardListRepository;
import com.sy.ticketingsystem.command.board.domain.model.fixture.BoardFixture;
import com.sy.ticketingsystem.core.domain.model.Error;
import io.vavr.control.Either.Left;
import io.vavr.control.Either.Right;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CreateCardCommandHandlerTest {

  private static Error AN_ERROR = Error.of("Operation cannot be completed");

  private static final CreateCardCommand COMMAND = CreateCardCommand.of(UUID.randomUUID(),
                                                                        "Card Name",
                                                                        "CARD DESCRIPTION");

  @Mock
  private BoardRepository boardRepository;

  @Mock
  private CardListRepository cardListRepository;

  private CreateCardCommandHandler underTest;

  @BeforeEach
  public void beforeEach() {

    underTest = new CreateCardCommandHandler(boardRepository, cardListRepository);
  }

  @DisplayName("Calling handler.handle(...) should return an error if the card list does not "
      + "exist.")
  @Test
  public void handle_returns_error_when_cardList_does_not_exist() {

    when(cardListRepository.getByIdOrErrorOut(any()))
        .thenReturn(left(AN_ERROR));

    var result = underTest.handle(COMMAND);
    Assertions.assertThat(result)
              .isInstanceOf(Left.class)
              .isEqualTo(left(AN_ERROR));
  }

  @DisplayName("Calling handler.handle(...) should return an error if the board  does not exist.")
  @Test
  public void handle_returns_error_when_board_does_not_exist(@Mock CardList cardList) {

    when(cardList.getBoardId())
        .thenReturn(BoardId.newBoardId());
    when(cardListRepository.getByIdOrErrorOut(any()))
        .thenReturn(right(cardList));
    when(boardRepository.getByIdOrErrorOut(any()))
        .thenReturn(left(AN_ERROR));

    var result = underTest.handle(COMMAND);
    Assertions.assertThat(result)
              .isInstanceOf(Left.class)
              .isEqualTo(left(AN_ERROR));
  }

  @DisplayName("Calling handler.handle(...) should return an error if the card cannot be "
      + "created.")
  @Test
  public void handle_returns_error_when_card_cannot_be_created(@Mock Board board,
                                                               @Mock CardList cardList) {

    when(cardListRepository.getByIdOrErrorOut(any()))
        .thenReturn(right(cardList));
    when(boardRepository.getByIdOrErrorOut(any()))
        .thenReturn(right(board));
    when(board.createCard(any(), any(), any()))
        .thenReturn(left(AN_ERROR));

    var result = underTest.handle(COMMAND);
    Assertions.assertThat(result)
              .isInstanceOf(Left.class)
              .isEqualTo(left(AN_ERROR));
  }

  @DisplayName("Calling handler.handle(...) should return an error if the card list cannot be "
      + "saved.")
  @Test
  public void handle_returns_error_when_cardList_cannot_be_saved(@Mock Board board,
                                                                 @Mock CardList cardList,
                                                                 @Mock Card card) {
    when(cardListRepository.getByIdOrErrorOut(any()))
        .thenReturn(right(cardList));
    when(boardRepository.getByIdOrErrorOut(any()))
        .thenReturn(right(board));
    when(board.createCard(any(), any(), any()))
        .thenReturn(right(card));
    when(cardListRepository.save(cardList))
        .thenReturn(left(AN_ERROR));

    var result = underTest.handle(COMMAND);
    Assertions.assertThat(result)
              .isInstanceOf(Left.class)
              .isEqualTo(left(AN_ERROR));
  }

  @DisplayName("Calling handler.handle(...) should return card when card is created.")
  @Test
  public void handle_returns_cardList_when_cardList_is_created() {

    var board = BoardFixture.newBoard("BOARD");
    var cardList = CardListFixture.newCardList(board.getBoardId(), "TODO LIST");
    var card = Card.of(CardId.newCardId(), cardList.getCardListId(), COMMAND.getName(), COMMAND.getDescription());

    when(cardListRepository.getByIdOrErrorOut(any()))
        .thenReturn(right(cardList));
    when(boardRepository.getByIdOrErrorOut(any()))
        .thenReturn(right(board));
    when(cardListRepository.save(cardList))
        .thenReturn(right(cardList));

    var result = underTest.handle(COMMAND);
    assertAll(
        () -> Assertions.assertThat(result)
                        .isInstanceOf(Right.class),
        () -> CardAssert.assertThat(result.get())
                        .hasCardListId(card.getCardListId())
                        .hasName(card.getName())
                        .hasDescription(card.getDescription())
    );
  }

}