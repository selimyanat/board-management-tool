package com.sy.board.management.command.board.domain.model.cardlist;

import com.sy.board.management.command.board.domain.model.BoardId;
import com.sy.board.management.core.domain.model.DomainEvent;
import lombok.ToString;
import lombok.Value;

@Value(staticConstructor = "of")
@ToString(callSuper = true)
public class CardCreatedEvent extends DomainEvent<CardList>  {

  public static final String CARD_CREATED = "CARD_CREATED";

  private CardId cardId;

  private CardListId cardListId;

  private BoardId boardId;

  private String name;

  private String description;

  @Override
  public CardList rehydrate(CardList cardList) {

    var card = Card.of(cardId, cardListId, name, description);
    cardList.getCards().add(card);
    return cardList;
  }

  @Override
  public String getEventName() {

    return CARD_CREATED;
  }
}
