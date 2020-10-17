package com.sy.ticketingsystem.core;

import com.sy.ticketingsystem.command.domain.board.Board;
import io.vavr.control.Either;

public abstract class CommandHandler <T extends Command> {

  public abstract Either<Error, Board> handle(T command);

}
