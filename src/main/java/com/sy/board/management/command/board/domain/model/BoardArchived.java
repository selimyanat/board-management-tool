package com.sy.board.management.command.board.domain.model;

import com.sy.board.management.command.board.domain.model.Board.Status;
import com.sy.board.management.core.domain.model.DomainEvent;
import lombok.Getter;
import lombok.ToString;
import lombok.Value;

@Getter
@ToString(callSuper = true)
@Value(staticConstructor = "of")
public class BoardArchived extends DomainEvent <Board> {

  public static final String BOARD_ARCHIVED = "BOARD_ARCHIVED";

  private BoardId boardId;

  private Status formerStatus;

  public Board rehydrate(Board board) {

    return board.setStatus(Status.ARCHIVED);
  }

  @Override
  public String getEventName() {

    return BOARD_ARCHIVED;
  }

  public Status getStatus() {

    return Status.ARCHIVED;
  }
}
