package com.sy.board.management.command.board.domain.model.cardlist;

import com.sy.board.management.command.board.domain.model.BoardId;

public class CardListFixture {

  public static CardList newCardList(BoardId boardId, String name) {

    return CardList.create(boardId, name).get();
  }

}
