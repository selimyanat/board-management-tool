package com.sy.board.management.command.board.domain.model;

import com.sy.board.management.command.board.domain.model.cardlist.CardListId;
import com.sy.board.management.core.domain.model.DomainEvent;
import lombok.ToString;
import lombok.Value;

@Value(staticConstructor = "of")
@ToString(callSuper = true)
public class BoardCardListCreated extends DomainEvent<Board> {

  public static final String BOARD_CARD_LIST_CREATED = "BOARD_CARD_LIST_CREATED";

  private CardListId cardListId;

  private int ordering;

  @Override
  public Board rehydrate(Board board) {

    var boardCardList = new BoardCardList(this.cardListId, ordering, board.getBoardId());
    board.getBoardCardLists().add(boardCardList);
    return board;
  }

  @Override
  public String getEventName() {

    return BOARD_CARD_LIST_CREATED;
  }
}
