package com.sy.ticketingsystem.query.board.domain.model;

import java.util.ArrayList;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString(callSuper = true)
public class CardList {

  public enum Status { ACTIVE, ARCHIVED };

  private String id;

  private String name;

  private Status status;

  private ArrayList<Card> cards;

  public static CardList of(String id, String name, Status status, ArrayList<Card> cards) {

    return new CardList(id, name, status, cards);
  }


  public static CardList of(String id, String name, Status status) {

    return of(id, name, status, new ArrayList<>());
  }


}
