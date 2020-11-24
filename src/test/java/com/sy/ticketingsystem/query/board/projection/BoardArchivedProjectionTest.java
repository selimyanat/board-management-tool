package com.sy.ticketingsystem.query.board.projection;

import static com.sy.ticketingsystem.command.board.domain.model.Board.Status.ACTIVE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.sy.ticketingsystem.command.board.domain.model.BoardArchived;
import com.sy.ticketingsystem.command.board.domain.model.BoardId;
import com.sy.ticketingsystem.query.board.domain.model.BoardView;
import com.sy.ticketingsystem.query.board.domain.model.BoardView.Status;
import com.sy.ticketingsystem.query.board.port.outgoing.adapter.BoardViewRepository;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BoardArchivedProjectionTest {

  private static final BoardArchived BOARD_ARCHIVED = BoardArchived.of(BoardId.newBoardId(), ACTIVE);

  @Mock
  private BoardViewRepository boardViewRepository;

  private BoardArchivedProjection underTest;

  @BeforeEach
  public void setUp() {

    underTest = new BoardArchivedProjection(boardViewRepository);
  }

  @DisplayName("On receiving a board archived event, the board view status is updated to archived.")
  @Test
  public void on_boardArchived_boardView_isUpdated(@Mock BoardView boardView) {

    when(boardViewRepository.findById(BOARD_ARCHIVED.getBoardId().getId())).thenReturn(Optional.of(boardView));

    underTest.on(BOARD_ARCHIVED);
    Assertions.assertAll(
        () -> verify(boardView).setStatus(Status.ARCHIVED),
        () -> verify(boardViewRepository).save(boardView),
        () -> verifyNoMoreInteractions(boardView)
    );
  }

  @DisplayName("On receiving a board archived event, for a board that does not exit the error is "
      + "logged.")
  @Test
  public void on_boardArchived_when_boardView_does_not_exist_logError() {

    when(boardViewRepository.findById(BOARD_ARCHIVED.getBoardId().getId())).thenReturn(Optional.empty());

    underTest.on(BOARD_ARCHIVED);
    verify(boardViewRepository, never()).save(any());
  }


}