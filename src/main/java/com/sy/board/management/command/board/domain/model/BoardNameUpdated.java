package com.sy.board.management.command.board.domain.model;

import com.sy.board.management.core.domain.model.DomainEvent;
import lombok.ToString;
import lombok.Value;

@Value(staticConstructor = "of")
@ToString(callSuper = true)
public class BoardNameUpdated extends DomainEvent <Board> {

  public static final String BOARD_NAME_UPDATED = "BOARD_NAME_UPDATED";

  private BoardId boardId;

  private String formerName;

  private String newName;

  public Board rehydrate(Board board) {

    return board.setName(this.newName);
  }

  @Override
  public String getEventName() {

    return BOARD_NAME_UPDATED;
  }

}
