package com.sy.ticketingsystem.command.board.usecase;

import com.sy.ticketingsystem.command.board.domain.model.Board;
import com.sy.ticketingsystem.core.command.Command;
import java.util.UUID;
import lombok.Value;

@Value
public class CreateBoardCommand implements Command<Board> {

  private UUID id;

  private String name;

  public static CreateBoardCommand newInstance(UUID id, String name) {

    return new CreateBoardCommand(id, name);
  }

}
