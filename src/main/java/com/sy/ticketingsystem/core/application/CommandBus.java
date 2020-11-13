package com.sy.ticketingsystem.core.application;

import com.sy.ticketingsystem.core.domain.model.Error;
import com.sy.ticketingsystem.core.domain.model.Unit;
import io.vavr.control.Either;

public interface CommandBus {

  Either<Error, Unit> accept(Command command);

}
