package com.sy.ticketingsystem.command.board.usecase;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;
import static java.lang.String.format;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.sy.ticketingsystem.command.board.domain.model.Board;
import com.sy.ticketingsystem.command.board.domain.model.BoardId;
import com.sy.ticketingsystem.command.board.domain.model.BoardRepository;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardList;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardListFixture;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardListId;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardListRepository;
import com.sy.ticketingsystem.command.board.domain.model.fixture.BoardFixture;
import com.sy.ticketingsystem.core.domain.model.Error;
import io.vavr.Tuple;
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
public class CreateCardListCommandHandlerTest {

  private static Error AN_ERROR = Error.of("Operation cannot be completed");

  private static final CreateCardListCommand COMMAND = CreateCardListCommand.of(UUID.randomUUID(), "TO_DO_LIST");

  @Mock
  private BoardRepository boardRepository;

  @Mock
  private CardListRepository cardListRepository;

  private CreateCardListCommandHandler underTest;

  @BeforeEach
  public void beforeEach() {

    underTest = new CreateCardListCommandHandler(boardRepository, cardListRepository);
  }

  @DisplayName("Calling handler.handle(...) should return an error if the board does not exist.")
  @Test
  public void handle_returns_error_when_board_does_not_exist() {

    when(boardRepository.getByIdOrErrorOut(any()))
        .thenReturn(left(AN_ERROR));

    var result = underTest.handle(COMMAND);
    Assertions.assertThat(result)
              .isInstanceOf(Left.class)
              .isEqualTo(left(AN_ERROR));
  }

  @DisplayName("Calling handler.handle(...) should return an error if the card list cannot be "
      + "archived.")
  @Test
  public void handle_returns_error_when_cardList_cannot_be_created(@Mock Board board) {

    when(boardRepository.getByIdOrErrorOut(any()))
        .thenReturn(right(board));
    when(board.addCardList(any()))
        .thenReturn(left(AN_ERROR));

    var result = underTest.handle(COMMAND);
    Assertions.assertThat(result)
              .isInstanceOf(Left.class)
              .isEqualTo(left(AN_ERROR));
  }

  @DisplayName("Calling handler.handle(...) should return an error if the board cannot be saved.")
  @Test
  public void handle_returns_error_when_board_cannot_be_saved(@Mock Board board,
                                                              @Mock CardList cardList) {

    when(boardRepository.getByIdOrErrorOut(any()))
        .thenReturn(right(board));
    when(board.addCardList(any()))
        .thenReturn(right(cardList));
    when(boardRepository.save(board))
        .thenReturn(left(AN_ERROR));

    var result = underTest.handle(COMMAND);
    Assertions.assertThat(result)
              .isInstanceOf(Left.class)
              .isEqualTo(left(AN_ERROR));
  }

  @DisplayName("Calling handler.handle(...) should return an error if the card list cannot be saved.")
  @Test
  public void handle_returns_error_when_cardList_cannot_be_saved(@Mock Board board,
                                                              @Mock CardList cardList) {

    when(boardRepository.getByIdOrErrorOut(any()))
        .thenReturn(right(board));
    when(board.addCardList(any()))
        .thenReturn(right(cardList));
    when(boardRepository.save(board))
        .thenReturn(right(board));
    when(cardListRepository.save(cardList))
        .thenReturn(left(AN_ERROR));

    var result = underTest.handle(COMMAND);
    Assertions.assertThat(result)
              .isInstanceOf(Left.class)
              .isEqualTo(left(AN_ERROR));
  }

  @DisplayName("Calling handler.handle(...) should return card list when card list is created.")
  @Test
  public void handle_returns_cardList_when_cardList_is_created() {

    var board = BoardFixture.newBoard("BOARD_NAME");
    var cardList = CardListFixture.newCardList(board.getBoardId(), COMMAND.getName());

    when(boardRepository.getByIdOrErrorOut(any()))
        .thenReturn(right(board));
    when(boardRepository.save(any()))
        .thenReturn(right(board));
    when(cardListRepository.save(any()))
        .thenReturn(right(cardList));

    var result = underTest.handle(COMMAND);
    Assertions.assertThat(result)
              .isInstanceOf(Right.class)
              .isEqualTo(right(cardList));
  }

}