package com.sy.ticketingsystem.command.board.domain.model;

import com.sy.ticketingsystem.command.board.domain.model.Board;

public final class BoardFixture {

  private static final String BOARD_NAME = "MY_BOARD";

  public static Board newBoard(String name) {

    return Board.create(name).get();
  }

  public static Board archived(String name) {

    return Board.create(name).get().archive().get();
  }

}
