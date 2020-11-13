package com.sy.ticketingsystem.command.application.board;

import static com.sy.ticketingsystem.command.application.board.CreateBoardCommand.newInstance;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.sy.ticketingsystem.command.domain.model.board.Board;
import com.sy.ticketingsystem.command.domain.model.board.BoardRepository;
import com.sy.ticketingsystem.core.domain.model.Error;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CreateBoardCommandHandlerTest {

  private static final CreateBoardCommand COMMAND = newInstance(UUID.randomUUID(), "BOARD_NAME");

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

    when(boardRepository.save(any())).thenReturn(left(Error.of("Could not save board!!!")));

    var result = underTest.handle(COMMAND);
    assertAll(
        () -> assertTrue(result.isLeft()),
        () -> assertEquals("Could not save board!!!", result.getLeft().getMessage())
    );
  }

  @DisplayName("Calling handler.handle(...) should return unit when board name is created.")
  @Test
  public void handle_returns_unit_when_board_name_is_created(@Mock Board board) {

    when(boardRepository.save(any())).thenReturn(right(board));

    var result = underTest.handle(COMMAND);
    assertAll(
        () -> assertTrue(result.isRight()),
        () -> assertEquals(board, result.get())
    );
  }

}