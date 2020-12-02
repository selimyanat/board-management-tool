package com.sy.board.management.query.board.projection;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.sy.board.management.command.board.domain.model.BoardId;
import com.sy.board.management.command.board.domain.model.BoardNameUpdated;
import com.sy.board.management.query.board.domain.model.BoardView;
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
public class BoardNameUpdatedProjectionTest {

  private static final BoardId BOARD_ID = BoardId.newBoardId();

  private static final BoardNameUpdated BOARD_NAME_UPDATED = BoardNameUpdated.of(BOARD_ID,
                                                                                 "BOARD_NAME",
                                                                                 "NEW_BOARD_NAME");

  @Mock
  private BoardViewRepository boardViewRepository;

  private BoardNameUpdatedProjection underTest;

  @BeforeEach
  public void setUp() {

    underTest = new BoardNameUpdatedProjection(boardViewRepository);
  }

  @DisplayName("On receiving a board name updated event, the board view name is updated "
      + " with the new name")
  @Test
  public void on_boardNameUpdated_boardView_isUpdated(@Mock BoardView boardView) {

    when(boardViewRepository.findById(BOARD_NAME_UPDATED.getBoardId().getId())).thenReturn(Optional.of(boardView));

    underTest.on(BOARD_NAME_UPDATED);
    Assertions.assertAll(
        () -> verify(boardView).setName(BOARD_NAME_UPDATED.getNewName()),
        () -> verify(boardViewRepository).save(boardView),
        () -> verify(boardView, times(0)).getId(),
        () -> verify(boardView, times(0)).getName(),
        () -> verify(boardView, times(0)).setId(any()),
        () -> verify(boardView, times(0)).setStatus(any()),
        () -> verify(boardView, times(0)).setCardLists(any()),
        () -> verifyNoMoreInteractions(boardView)
    );
  }

  @DisplayName("On receiving a board name updated event, for a board that does not exit the error is "
      + "logged.")
  @Test
  public void on_boardNameUpdated_when_boardView_does_not_exist_logError() {

    when(boardViewRepository.findById(BOARD_NAME_UPDATED.getBoardId().getId())).thenReturn(Optional.empty());

    underTest.on(BOARD_NAME_UPDATED);
    verify(boardViewRepository, never()).save(any());
  }


}