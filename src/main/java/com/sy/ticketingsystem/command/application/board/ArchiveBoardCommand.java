package com.sy.ticketingsystem.command.application.board;

import com.sy.ticketingsystem.core.application.Command;
import java.util.UUID;
import lombok.Value;

@Value
public class ArchiveBoardCommand implements Command {

  private UUID boardId;

  public static ArchiveBoardCommand newInstance(UUID boardId) {

    return new ArchiveBoardCommand(boardId);
  }

}
