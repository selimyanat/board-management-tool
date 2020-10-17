package com.sy.ticketingsystem.command.application.board;

import com.sy.ticketingsystem.core.Command;
import java.util.UUID;
import lombok.Builder;

@Builder
public class UpdateBoardNameCommand extends Command {

  public UUID id;

  public String newName;


}
