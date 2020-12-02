package com.sy.board.management.command.board.usecase;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.sy.board.management.command.board.domain.model.Board;
import com.sy.board.management.command.board.domain.model.BoardId;
import com.sy.board.management.command.board.domain.model.BoardRepository;
import com.sy.board.management.core.domain.model.Error;
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
public class CreateBoardCommandHandlerTest {

  private static Error AN_ERROR = Error.of("Operation cannot be completed");

  private static final CreateBoardCommand COMMAND = CreateBoardCommand
      .of(UUID.randomUUID(), "BOARD_NAME");

  @Mock
  private BoardRepository boardRepository;

  private CreateBoardCommandHandler underTest;

  @BeforeEach
  public void beforeEach() {

    underTest = new CreateBoardCommandHandler(boardRepository);
  }

  @DisplayName("Calling handler.handle(...) should return an error if the board cannot be saved.")
  @Test
  public void handle_returns_error_when_board_cannot_be_saved() {

    when(boardRepository.save(any()))
        .thenReturn(left(AN_ERROR));

    var result = underTest.handle(COMMAND);
    Assertions.assertThat(result)
              .isInstanceOf(Left.class)
              .isEqualTo(left(AN_ERROR));
  }

  @DisplayName("Calling handler.handle(...) should return unit when board name is created.")
  @Test
  public void handle_returns_unit_when_board_name_is_created(@Mock Board board) {

    when(board.getBoardId())
        .thenReturn(BoardId.newBoardId());
    when(boardRepository.save(any()))
        .thenReturn(right(board));

    var result = underTest.handle(COMMAND);
    Assertions.assertThat(result)
              .isInstanceOf(Right.class)
              .isEqualTo(right(board));
  }

}