package com.sy.ticketingsystem.core.command;

import com.sy.ticketingsystem.core.domain.model.Error;
import io.vavr.control.Either;

public interface CommandBus {

  <T> Either<Error, T> accept(Command<T> command);

}
