package com.sy.board.management.query.board.domain.model;

import com.sy.board.management.query.board.domain.model.BoardView.Status;
import com.sy.board.management.command.board.domain.model.BoardId;
import java.util.ArrayList;

public final class BoardViewFixture {

  public static final BoardView of(BoardId boardId, String name, Status status) {

    return BoardView.of(boardId.getId(), name, status);
  }

  public static final BoardView of(BoardId boardId, String name, Status status, CardList cardList) {

    var cardLists = new ArrayList<CardList>();
    cardLists.add(cardList);
    return BoardView.of(boardId.getId(), name, status, cardLists);
  }
}
