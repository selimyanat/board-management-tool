package com.sy.ticketingsystem.command.domain.model.board;

import com.sy.ticketingsystem.command.domain.model.board.Board.Status;
import com.sy.ticketingsystem.core.domain.model.DomainEvent;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class BoardArchived extends DomainEvent <Board> {

  public static final String BOARD_ARCHIVED = "BOARD_ARCHIVED";

  private BoardArchived() {
    super();
  }

  public static BoardArchived newInstance() {

    return new BoardArchived();
  }

  public Board rehydrate(Board board) {

    return board.toBuilder().status(Status.ARCHIVED).build();

  }

  @Override
  public String getEventName() {

    return BOARD_ARCHIVED;
  }
}
