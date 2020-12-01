package com.sy.ticketingsystem.command.board.domain.model.cardlist;

import com.sy.ticketingsystem.core.domain.model.AbstractId;
import java.util.UUID;
import lombok.ToString;

@ToString(callSuper = true)
public class CardId extends AbstractId {

  public CardId(String id) {
    super(id);
  }

  public static CardId newCardId() {
    return new CardId(UUID.randomUUID().toString());
  }

  public static CardId fromExisting(String id) {

    return new CardId(UUID.fromString(id).toString());
  }
}
