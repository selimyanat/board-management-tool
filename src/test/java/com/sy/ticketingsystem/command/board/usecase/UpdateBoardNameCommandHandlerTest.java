package com.sy.ticketingsystem.command.board.usecase;

import static com.sy.ticketingsystem.command.board.usecase.UpdateBoardNameCommand.of;
import static com.sy.ticketingsystem.core.domain.model.Unit.unit;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.sy.ticketingsystem.command.board.domain.model.Board;
import com.sy.ticketingsystem.command.board.domain.model.BoardId;
import com.sy.ticketingsystem.command.board.domain.model.BoardRepository;
import com.sy.ticketingsystem.core.domain.model.Error;
import com.sy.ticketingsystem.core.domain.model.Unit;
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
public class UpdateBoardNameCommandHandlerTest {

  private static Error AN_ERROR = Error.of("Operation cannot be completed");

  private static final UpdateBoardNameCommand COMMAND = of(UUID.randomUUID(), "NEW_NAME");

  @Mock
  private BoardRepository boardRepository;

  private UpdateBoardNameCommandHandler underTest;

  @BeforeEach
  public void beforeEach() {

    underTest = new UpdateBoardNameCommandHandler(boardRepository);
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

  @DisplayName("Calling handler.handle(...) should return an error if the board name cannot be updated.")
  @Test
  public void handle_returns_error_when_board_name_cannot_be_updated(@Mock Board board) {

    when(boardRepository.getByIdOrErrorOut(any()))
        .thenReturn(right(board));
    when(board.updateName(COMMAND.getNewName()))
        .thenReturn(left(AN_ERROR));

    var result = underTest.handle(COMMAND);
    Assertions.assertThat(result)
              .isInstanceOf(Left.class)
              .isEqualTo(left(AN_ERROR));
  }


  @DisplayName("Calling handler.handle(...) should return an error if the board cannot be saved.")
  @Test
  public void handle_returns_error_when_board_cannot_be_saved(@Mock Board board) {

    when(boardRepository.getByIdOrErrorOut(any()))
        .thenReturn(right(board));
    when(board.updateName(COMMAND.getNewName()))
        .thenReturn(right(unit()));
    when(boardRepository.save(board))
        .thenReturn(left(AN_ERROR));

    var result = underTest.handle(COMMAND);
    Assertions.assertThat(result)
              .isInstanceOf(Left.class)
              .isEqualTo(left(AN_ERROR));
  }

  @DisplayName("Calling handler.handle(...) should return unit when board name is updated.")
  @Test
  public void handle_returns_board_when_board_name_is_updated(@Mock Board board) {

    when(board.getBoardId())
        .thenReturn(BoardId.newBoardId());
    when(boardRepository.getByIdOrErrorOut(any()))
        .thenReturn(right(board));
    when(board.updateName(COMMAND.getNewName()))
        .thenReturn(right(unit()));
    when(boardRepository.save(board))
        .thenReturn(right(board));

    var result = underTest.handle(COMMAND);
    Assertions.assertThat(result)
              .isInstanceOf(Right.class)
              .isEqualTo(right(board));
  }

}