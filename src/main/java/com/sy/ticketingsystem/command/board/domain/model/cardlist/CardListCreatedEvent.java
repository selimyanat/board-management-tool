package com.sy.ticketingsystem.command.board.domain.model.cardlist;

import com.sy.ticketingsystem.command.board.domain.model.BoardId;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardList.Status;
import com.sy.ticketingsystem.core.domain.model.DomainEvent;
import lombok.ToString;
import lombok.Value;

@Value(staticConstructor = "of")
@ToString(callSuper = true)
public class CardListCreatedEvent extends DomainEvent<CardList> {

  public static final String CARD_LIST_CREATED = "CARD_LIST_CREATED";

  private CardListId cardListId;

  private BoardId boardId;

  private String name;

  @Override
  public CardList rehydrate(CardList cardList) {

    cardList.setBoardId(this.boardId)
            .setName(this.name)
            .setStatus(Status.ACTIVE);

    return cardList;
  }

  @Override
  public String getEventName() {

    return CARD_LIST_CREATED;
  }
}
