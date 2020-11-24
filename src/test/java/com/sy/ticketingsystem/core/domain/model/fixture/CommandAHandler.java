package com.sy.ticketingsystem.core.domain.model.fixture;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

import com.sy.ticketingsystem.core.command.CommandHandler;
import com.sy.ticketingsystem.core.domain.model.Error;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class CommandAHandler implements CommandHandler<CommandA, DomainObjectA>  {

  @Override
  public Either<Error, DomainObjectA> handle(CommandA command) {
    return command.isResult() ? right(DomainObjectA.of()) : left(Error.of("an error occurred"));
  }

}
