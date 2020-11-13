package com.sy.ticketingsystem.command.application.board;

import com.fasterxml.jackson.databind.deser.std.UUIDDeserializer;
import com.sy.ticketingsystem.core.application.Command;
import java.util.UUID;
import lombok.Value;

@Value
public class CreateBoardCommand implements Command {

  private UUID id;

  private String name;

  public static CreateBoardCommand newInstance(UUID id, String name) {

    return new CreateBoardCommand(id, name);
  }

}
