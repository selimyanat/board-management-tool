package com.sy.ticketingsystem.core.domain.model.fixture;

import com.sy.ticketingsystem.core.command.Command;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "of")
public class CommandB implements Command<Boolean> {

  private boolean result;

  public static CommandB withTrueResult() {

    return of(true);
  }

  public static CommandB withFalseResult() {

    return of(false);
  }
}
