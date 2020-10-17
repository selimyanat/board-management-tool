package com.sy.ticketingsystem.command.application.board;

import static com.sy.ticketingsystem.command.application.board.ArchiveBoardCommand.newInstance;
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

import com.sy.ticketingsystem.command.domain.model.board.Board;
import com.sy.ticketingsystem.command.domain.model.board.BoardId;
import com.sy.ticketingsystem.command.domain.model.board.BoardRepository;
import com.sy.ticketingsystem.core.domain.model.Error;
import io.vavr.control.Either;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ArchiveBoardCommandHandlerTest {

  private static final ArchiveBoardCommand COMMAND = newInstance(UUID.randomUUID());

  @Mock
  private BoardRepository boardRepository;

  private ArchiveBoardCommandHandler underTest;

  @BeforeEach
  public void beforeEach() {

    underTest = new ArchiveBoardCommandHandler(boardRepository);
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

  @DisplayName("Calling handler.handle(...) should return an error if the board cannot be archived.")
  @Test
  public void handle_returns_error_when_board_cannot_be_archived(@Mock Board board) {

    when(boardRepository.findById(any(BoardId.class))).thenReturn(right(some(board)));
    when(board.archive()).thenReturn(left(Error.of("Something bad happen!!!")));

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
    when(board.archive()).thenReturn(Either.right(board));
    when(boardRepository.save(board)).thenReturn(left(Error.of("Could not save board!!!")));

    var result = underTest.handle(COMMAND);
    assertAll(
        () -> assertTrue(result.isLeft()),
        () -> assertEquals("Could not save board!!!", result.getLeft().getMessage())
    );
  }

  @DisplayName("Calling handler.handle(...) should return unit when board is archived.")
  @Test
  public void handle_returns_unit_when_board_is_archived(@Mock Board board) {

    when(boardRepository.findById(any(BoardId.class))).thenReturn(right(some(board)));
    when(board.archive()).thenReturn(right(board));
    when(boardRepository.save(board)).thenReturn(right(board));

    var result = underTest.handle(COMMAND);
    assertAll(
        () -> assertTrue(result.isRight()),
        () -> assertEquals(board, result.get())
    );
  }

}