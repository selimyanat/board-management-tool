package com.sy.board.management.query.board.projection;

import static org.mockito.Mockito.verify;

import com.sy.board.management.query.board.domain.model.BoardView;
import com.sy.board.management.query.board.domain.model.BoardView.Status;
import com.sy.board.management.query.board.port.outgoing.adapter.BoardViewRepository;
import com.sy.board.management.command.board.domain.model.BoardCreated;
import com.sy.board.management.command.board.domain.model.BoardId;
import com.sy.board.management.query.board.domain.model.BoardViewAssert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BoardCreatedProjectionTest {

  private static final BoardCreated BOARD_CREATED = BoardCreated.of(BoardId.newBoardId(), "BOARD_NAME");

  @Mock
  private BoardViewRepository boardViewRepository;

  private BoardCreatedProjection underTest;

  @BeforeEach
  public void setUp() {

    underTest = new BoardCreatedProjection(boardViewRepository);
  }

  @DisplayName("On receiving a board created event, a board view is created.")
  @Test
  public void on_boardCreated_boardView_isCreated() {

    var boardCaptor = ArgumentCaptor.forClass(BoardView.class);

    underTest.on(BOARD_CREATED);
    verify(boardViewRepository).save(boardCaptor.capture());

    var boardView = boardCaptor.getValue();
    BoardViewAssert.assertThat(boardView)
                   .hasId(BOARD_CREATED.getBoardId().getId())
                   .hasName(BOARD_CREATED.getBoardName())
                   .hasStatus(Status.ACTIVE)
                   .hasNoCardLists();
  }


}