package com.sy.ticketingsystem.command.domain.model.board;

import com.sy.ticketingsystem.core.domain.model.AbstractId;
import java.util.UUID;
import lombok.ToString;

@ToString(callSuper = true)
public class BoardId extends AbstractId {

  public BoardId(String id) {
    super(id);
  }

  public static BoardId newBoardId() {
    return new BoardId(UUID.randomUUID().toString());
  }

  public static BoardId fromExisting(String id) {

    return new BoardId(UUID.fromString(id).toString());
  }
}
