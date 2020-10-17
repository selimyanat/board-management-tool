package com.sy.ticketingsystem.command.domain.board;

import com.sy.ticketingsystem.command.domain.board.Board.Status;
import com.sy.ticketingsystem.core.DomainEvent;
import lombok.Builder;

@Builder
public class BoardArchivedEvent extends DomainEvent <Board> {

  public long id;

  public Status status;

  public Board rehydrate(Board board) {

    return Board.builder()
                .id(this.id)
                .name(board.name)
                .status(this.status)
                .version(board.version)
                .build();
  }
}
