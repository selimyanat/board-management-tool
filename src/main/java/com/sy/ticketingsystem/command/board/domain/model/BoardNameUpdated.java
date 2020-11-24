package com.sy.ticketingsystem.command.board.domain.model;

import com.sy.ticketingsystem.core.domain.model.DomainEvent;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class BoardNameUpdated extends DomainEvent <Board> {

  public static final String BOARD_NAME_UPDATED = "BOARD_NAME_UPDATED";

  private String formerName;

  private String newName;

  private BoardNameUpdated(String formerName, String newName) {

    super();
    this.formerName = formerName;
    this.newName = newName;
  }

  public static BoardNameUpdated newInstance(String formerName, String newName) {

    return new BoardNameUpdated(formerName, newName);
  }

  public Board rehydrate(Board board) {

    return board.setName(this.newName);
  }

  @Override
  public String getEventName() {

    return BOARD_NAME_UPDATED;
  }

}
