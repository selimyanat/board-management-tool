package com.sy.ticketingsystem.command.board.usecase;

import com.sy.ticketingsystem.command.board.domain.model.Board;
import com.sy.ticketingsystem.core.command.Command;
import java.util.UUID;
import lombok.Value;

@Value(staticConstructor = "of")
public class UpdateBoardNameCommand implements Command<Board> {

  private UUID boardId;

  private String newName;


}
