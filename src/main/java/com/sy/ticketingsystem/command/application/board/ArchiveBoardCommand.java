package com.sy.ticketingsystem.command.application.board;

import com.sy.ticketingsystem.core.Command;
import java.util.UUID;
import lombok.Builder;

@Builder
public class ArchiveBoardCommand extends Command {

  public UUID id;

}
