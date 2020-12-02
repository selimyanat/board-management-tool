package com.sy.board.management.core.command;

import com.sy.board.management.core.domain.model.Error;
import io.vavr.control.Either;

public interface CommandBus {

  <T> Either<Error, T> accept(Command<T> command);

}
