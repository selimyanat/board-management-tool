package com.sy.board.management.command.board.domain.model.cardlist;

import com.sy.board.management.core.domain.model.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@ToString(callSuper = true)
public class Card {

  private final CardId cardId;

  private CardListId cardListId;

  private String name;

  private String description;


  private Card(CardId cardId, CardListId cardListId, String name, String description) {

    this.cardId = cardId;
    this.cardListId = cardListId;
    this.name = name;
    this.description = description;
  }

  public static Card of(CardId cardId, CardListId cardListId, String name, String description) {

    return new Card(cardId, cardListId, name, description);
  }

}
