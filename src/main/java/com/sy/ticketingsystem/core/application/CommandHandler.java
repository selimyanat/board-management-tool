package com.sy.ticketingsystem.core.application;

import com.sy.ticketingsystem.core.domain.model.Error;
import io.vavr.control.Either;

public interface CommandHandler <T extends Command, R> {

  Either<Error, R> handle(T command);

}
