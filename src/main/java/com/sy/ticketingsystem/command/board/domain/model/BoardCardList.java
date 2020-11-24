package com.sy.ticketingsystem.command.board.domain.model;

import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardListId;
import lombok.ToString;
import lombok.Value;


@Value
@ToString(callSuper = true)
public class BoardCardList {

  private CardListId cardListId;

  private int ordering;

  private BoardId boardId;



}
