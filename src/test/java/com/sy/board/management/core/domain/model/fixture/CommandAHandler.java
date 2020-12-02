package com.sy.board.management.core.domain.model.fixture;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

import com.sy.board.management.core.command.CommandHandler;
import com.sy.board.management.core.domain.model.Error;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class CommandAHandler implements CommandHandler<CommandA, DomainObjectA>  {

  @Override
  public Either<Error, DomainObjectA> handle(CommandA command) {
    return command.isResult() ? right(DomainObjectA.of()) : left(Error.of("an error occurred"));
  }

}
