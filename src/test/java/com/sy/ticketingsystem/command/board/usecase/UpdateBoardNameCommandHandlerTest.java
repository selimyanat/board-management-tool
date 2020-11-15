package com.sy.ticketingsystem.command.board.usecase;

import static com.sy.ticketingsystem.command.board.usecase.UpdateBoardNameCommand.newInstance;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;
import static java.lang.String.format;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.sy.ticketingsystem.command.board.domain.model.Board;
import com.sy.ticketingsystem.command.board.domain.model.BoardId;
import com.sy.ticketingsystem.command.board.domain.model.BoardRepository;
import com.sy.ticketingsystem.command.board.usecase.UpdateBoardNameCommand;
import com.sy.ticketingsystem.command.board.usecase.UpdateBoardNameCommandHandler;
import com.sy.ticketingsystem.core.domain.model.Error;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UpdateBoardNameCommandHandlerTest {

  private static final UpdateBoardNameCommand COMMAND = newInstance(UUID.randomUUID(), "NEW_NAME");

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

    when(boardRepository.findById(any(BoardId.class))).thenReturn(right(none()));

    var result = underTest.handle(COMMAND);
    assertAll(
        () -> assertTrue(result.isLeft()),
        () -> assertEquals(format("Board with id %s does not exist. The board cannot be archived.",
                                  COMMAND.getBoardId()
                           ),
                           result.getLeft().getMessage()
        )
    );
  }

  @DisplayName("Calling handler.handle(...) should return an error if the board name cannot be updated.")
  @Test
  public void handle_returns_error_when_board_name_cannot_be_updated(@Mock Board board) {

    when(boardRepository.findById(any(BoardId.class))).thenReturn(right(some(board)));
    when(board.updateName(COMMAND.getNewName())).thenReturn(left(Error.of("Something bad happen!!!")));

    var result = underTest.handle(COMMAND);
    assertAll(
        () -> assertTrue(result.isLeft()),
        () -> assertEquals("Something bad happen!!!", result.getLeft().getMessage())
    );
  }


  @DisplayName("Calling handler.handle(...) should return an error if the board cannot be saved.")
  @Test
  public void handle_returns_error_when_board_cannot_be_saved(@Mock Board board) {

    when(boardRepository.findById(any(BoardId.class))).thenReturn(right(some(board)));
    when(board.updateName(COMMAND.getNewName())).thenReturn(right(board));
    when(boardRepository.save(board)).thenReturn(left(Error.of("Could not save board!!!")));

    var result = underTest.handle(COMMAND);
    assertAll(
        () -> assertTrue(result.isLeft()),
        () -> assertEquals("Could not save board!!!", result.getLeft().getMessage())
    );
  }

  @DisplayName("Calling handler.handle(...) should return unit when board name is updated.")
  @Test
  public void handle_returns_unit_when_board_name_is_updated(@Mock Board board) {

    when(board.getBoardId()).thenReturn(BoardId.newBoardId());
    when(boardRepository.findById(any(BoardId.class))).thenReturn(right(some(board)));
    when(board.updateName(COMMAND.getNewName())).thenReturn(right(board));
    when(boardRepository.save(board)).thenReturn(right(board));

    var result = underTest.handle(COMMAND);
    assertAll(
        () -> assertTrue(result.isRight()),
        () -> assertEquals(board, result.get())
    );
  }

}