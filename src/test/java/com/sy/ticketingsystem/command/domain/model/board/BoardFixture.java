package com.sy.ticketingsystem.command.domain.model.board;

import com.sy.ticketingsystem.command.domain.model.board.Board.Status;

public final class BoardFixture {

  public static Board newBoard(String name) {

    return Board.builder()
                .boardId(BoardId.newBoardId())
                .name(name)
                .status(Status.ACTIVE)
                .build();
  }

}
