package com.sy.board.management.command.board.domain.model.fixture;

import com.sy.board.management.command.board.domain.model.Board;

public final class BoardFixture {

  public static Board newBoard(String name) {

    return Board.create(name).get();
  }

  public static Board archivedBoard(String name) {

    var board = newBoard(name);
    board.archive();
    return board;
  }

  public static Board updatedBoardName(String formerName, String newName) {

    var board = newBoard(formerName);
    board.updateName(newName);
    return board;
  }

}
