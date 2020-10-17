package com.sy.ticketingsystem.command.domain.model.board;

import com.sy.ticketingsystem.core.domain.model.DomainEvent;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class BoardNameUpdated extends DomainEvent <Board> {

  public static final String BOARD_NAME_UPDATED = "BOARD_NAME_UPDATED";

  public String newName;

  private BoardNameUpdated(String newName) {

    super();
    this.newName = newName;
  }

  public static BoardNameUpdated newInstance(String newName) {

    return new BoardNameUpdated(newName);
  }

  public Board rehydrate(Board board) {

    return board.toBuilder().name(this.newName).build();
  }

  @Override
  public String getEventName() {

    return BOARD_NAME_UPDATED;
  }

}
