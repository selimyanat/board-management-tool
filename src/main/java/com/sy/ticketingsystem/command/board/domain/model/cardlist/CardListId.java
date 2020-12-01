package com.sy.ticketingsystem.command.board.domain.model.cardlist;

import com.sy.ticketingsystem.core.domain.model.AbstractId;
import java.util.UUID;
import lombok.ToString;

@ToString(callSuper = true)
public class CardListId extends AbstractId {

  public CardListId(String id) {
    super(id);
  }

  public static CardListId newCardListId() {
    return new CardListId(UUID.randomUUID().toString());
  }

  public static CardListId fromExisting(String id) {

    return new CardListId(UUID.fromString(id).toString());
  }

}
