package com.sy.ticketingsystem.command.domain.board;

import com.sy.ticketingsystem.core.DomainEvent;
import lombok.Builder;

@Builder
public class BoardNameUpdatedEvent extends DomainEvent <Board> {

  public String newName;

  public int version;

  public Board rehydrate(Board board) {

    return Board.builder()
                .id(board.id)
                .name(this.newName)
                .status(board.status)
                .version(board.version + 1)
                .build();
  }

}
