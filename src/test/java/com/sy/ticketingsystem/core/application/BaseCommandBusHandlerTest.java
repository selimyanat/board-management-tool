package com.sy.ticketingsystem.core.application;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sy.ticketingsystem.core.application.BaseCommandBusHandlerTest.CommandBHandler.CommandB;
import com.sy.ticketingsystem.core.application.BaseCommandBusHandlerTest.CommandBHandler.DomainObjectB;
import com.sy.ticketingsystem.core.domain.model.Error;
import io.vavr.control.Either;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Value;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class BaseCommandBusHandlerTest {

  private BaseCommandBusHandler underTest;

  @DisplayName("Calling commandBus.accept(command) execute the command thanks to the appropriate "
      + "handler")
  @Test
  public void accept_call_handler_to_execute_theCommand() {

    underTest = new BaseCommandBusHandler(List.of(
        new CommandAHandler(),
        new CommandBHandler()));

    assertAll(
        () -> assertTrue(underTest.accept(new CommandA(true)).isRight()),
        () -> assertTrue(underTest.accept(new CommandB(false)).isLeft())
    );
  }


  @Test
  public void accept_when_command_is_not_supported_returns_error() {

    underTest = new BaseCommandBusHandler(List.of(new CommandAHandler()));

    assertAll(
        () -> assertTrue(underTest.accept(new CommandB(false)).isLeft()),
        () -> assertEquals("Command not supported", underTest.accept(new CommandB(false)).getLeft().getMessage())
    );
  }

  static class CommandAHandler implements CommandHandler<CommandA, DomainObjectA> {

    @Override
    public Either<Error, DomainObjectA> handle(CommandA command) {
      return command.result ? right(new DomainObjectA()) : left(Error.of("an error occurred"));
    }
  }

  @Value
  @AllArgsConstructor
  static class CommandA implements Command {
    boolean result;
  }

  static class DomainObjectA {}

  static class CommandBHandler implements CommandHandler<CommandB, DomainObjectB> {

    @Override
    public Either<Error, DomainObjectB> handle(CommandB command) {
      return command.result ? right(new DomainObjectB()) : left(Error.of("an error occurred"));
    }

    static class DomainObjectB {}

    @Value
    @AllArgsConstructor
    static class CommandB implements Command {
      boolean result;
    }
  }
}