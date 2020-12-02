package com.sy.board.management.core.command;

import com.sy.board.management.core.domain.model.Error;
import io.vavr.control.Either;

public interface CommandHandler <T extends Command, R> {

  Either<Error, R> handle(T command);

}
