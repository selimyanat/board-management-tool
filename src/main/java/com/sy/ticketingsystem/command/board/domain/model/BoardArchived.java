package com.sy.ticketingsystem.command.board.domain.model;

import com.sy.ticketingsystem.command.board.domain.model.Board.Status;
import com.sy.ticketingsystem.core.domain.model.DomainEvent;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class BoardArchived extends DomainEvent <Board> {

  public static final String BOARD_ARCHIVED = "BOARD_ARCHIVED";

  private Status formerStatus;

  private BoardArchived(Status formerStatus) {
    super();
    this.formerStatus = formerStatus;
  }

  public static BoardArchived newInstance() {

    return new BoardArchived(Status.ACTIVE);
  }

  public Board rehydrate(Board board) {

    return board.setStatus(Status.ARCHIVED);
  }

  @Override
  public String getEventName() {

    return BOARD_ARCHIVED;
  }
}
