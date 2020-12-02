package com.sy.board.management.core.command;

import static io.vavr.control.Either.left;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.sy.board.management.core.domain.model.Error;
import com.sy.board.management.core.domain.model.fixture.CommandA;
import com.sy.board.management.core.domain.model.fixture.CommandAHandler;
import com.sy.board.management.core.domain.model.fixture.CommandB;
import com.sy.board.management.core.domain.model.fixture.CommandBHandler;
import io.vavr.control.Either.Left;
import io.vavr.control.Either.Right;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class TransactionalCommandBusHandlerTest {

  private TransactionalCommandBusHandler underTest;

  @DisplayName("Calling commandBus.accept(command) execute the command thanks to the appropriate "
      + "handler")
  @Test
  public void accept_call_handler_to_execute_theCommand() {

    underTest = new TransactionalCommandBusHandler(List.of(CommandAHandler.of(), CommandBHandler.of()));


    assertAll(
        () -> Assertions.assertThat(underTest.accept(CommandA.withTrueResult()))
                        .isInstanceOf(Right.class),
        () -> Assertions.assertThat(underTest.accept(CommandB.withFalseResult()))
                        .isInstanceOf(Left.class)
    );
  }


  @Test
  public void accept_when_command_is_not_supported_returns_error() {

    underTest = new TransactionalCommandBusHandler(List.of(CommandAHandler.of()));

    Assertions.assertThat(underTest.accept(CommandB.withFalseResult()))
              .isInstanceOf(Left.class)
              .isEqualTo(left(Error.of("Command not supported")));
  }
}