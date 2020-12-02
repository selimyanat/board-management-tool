package com.sy.board.management.command.board.domain.model;

import com.sy.board.management.command.board.domain.model.cardlist.CardListId;
import lombok.ToString;
import lombok.Value;


@Value
@ToString(callSuper = true)
public class BoardCardList {

  private CardListId cardListId;

  private int ordering;

  private BoardId boardId;



}
