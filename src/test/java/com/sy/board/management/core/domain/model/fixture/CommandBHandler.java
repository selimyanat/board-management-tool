package com.sy.board.management.core.domain.model.fixture;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

import com.sy.board.management.core.command.CommandHandler;
import com.sy.board.management.core.domain.model.Error;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class CommandBHandler implements CommandHandler<CommandB, DomainObjectB>  {

  @Override
  public Either<Error, DomainObjectB> handle(CommandB command) {
    return command.isResult() ? right(DomainObjectB.of()) : left(Error.of("an error occurred"));
  }

}
