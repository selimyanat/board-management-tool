package com.sy.ticketingsystem.command.board.usecase;

import com.sy.ticketingsystem.command.board.domain.model.Board;
import com.sy.ticketingsystem.core.command.Command;
import java.util.UUID;
import lombok.Value;

@Value
public class ArchiveBoardCommand implements Command<Board> {

  private UUID boardId;

  public static ArchiveBoardCommand newInstance(UUID boardId) {

    return new ArchiveBoardCommand(boardId);
  }

}