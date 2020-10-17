package com.sy.ticketingsystem.command.application.board;

import com.sy.ticketingsystem.core.application.Command;
import java.util.UUID;

public abstract class AbstractBoardCommand implements Command {

  protected UUID boardId;
}
