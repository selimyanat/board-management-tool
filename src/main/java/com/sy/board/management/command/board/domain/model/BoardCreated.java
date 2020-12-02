package com.sy.board.management.command.board.domain.model;

import com.sy.board.management.command.board.domain.model.Board.Status;
import com.sy.board.management.core.domain.model.DomainEvent;
import lombok.ToString;
import lombok.Value;

@Value(staticConstructor = "of")
@ToString(callSuper = true)
public class BoardCreated extends DomainEvent <Board> {

  public static final String BOARD_CREATED = "BOARD_CREATED";

  private BoardId boardId;

  private String boardName;

  public BoardCreated(BoardId id, String boardName) {

    super();
    this.boardId = id;
    this.boardName = boardName;
  }

  public Board rehydrate(Board board) {

    return board.setName(this.boardName)
                .setStatus(Status.ACTIVE);
  }

  @Override
  public String getEventName() {

    return BOARD_CREATED;
  }
}
