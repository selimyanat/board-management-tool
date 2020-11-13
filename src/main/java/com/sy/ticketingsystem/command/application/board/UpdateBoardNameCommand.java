package com.sy.ticketingsystem.command.application.board;

import com.sy.ticketingsystem.core.application.Command;
import java.util.UUID;
import lombok.Value;

@Value
public class UpdateBoardNameCommand implements Command {

  private UUID boardId;

  private String newName;

  public static UpdateBoardNameCommand newInstance(UUID boardId, String newName) {

    return new UpdateBoardNameCommand(boardId, newName);
  }

}
