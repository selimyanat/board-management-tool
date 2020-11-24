package com.sy.ticketingsystem.command.board.usecase;

import com.sy.ticketingsystem.command.board.domain.model.Board;
import com.sy.ticketingsystem.core.command.Command;
import java.util.UUID;
import lombok.Value;

@Value
public class UpdateBoardNameCommand implements Command<Board> {

  private UUID boardId;

  private String newName;

  public static UpdateBoardNameCommand newInstance(UUID boardId, String newName) {

    return new UpdateBoardNameCommand(boardId, newName);
  }

}
