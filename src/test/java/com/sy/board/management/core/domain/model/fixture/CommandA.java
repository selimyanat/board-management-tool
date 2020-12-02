package com.sy.board.management.core.domain.model.fixture;

import com.sy.board.management.core.command.Command;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(staticName = "of")
public class CommandA implements Command<Boolean>  {

  private boolean result;

  public static CommandA withTrueResult() {

    return of(true);
  }

  public static CommandA withFalseResult() {

    return of(false);
  }
}
