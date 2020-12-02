package com.sy.board.management.query.board.domain.model;


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
public class Card {

  private String id;

  private String name;

  private String description;


  public static Card of(String id, String name, String description) {

    return new Card(id, name, description);
  }
}
