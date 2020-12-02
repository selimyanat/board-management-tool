package com.sy.board.management.command.board.domain.model.cardlist;

import com.sy.board.management.core.domain.model.AbstractId;
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
