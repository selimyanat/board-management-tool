package com.sy.ticketingsystem.command.domain.board;

import com.sy.ticketingsystem.command.domain.board.Board.Status;
import com.sy.ticketingsystem.core.DomainEvent;
import java.util.UUID;
import lombok.Builder;

@Builder
public class BoardCreatedEvent extends DomainEvent <Board> {

  public UUID id;

  public String name;

  public Status status;

  public Board rehydrate(Board board) {

    return Board.builder()
                .id(this.id)
                .name(this.name)
                .status(this.status)
                .version(board.version + 1)
                .build();
  }

}
