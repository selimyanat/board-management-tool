package com.sy.board.management.query.board.domain.model;

import java.util.ArrayList;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "board_view")
@Getter
@Setter
@EqualsAndHashCode
@ToString(callSuper = true)
public class BoardView {

  public enum Status { ACTIVE, ARCHIVED };

  @Id
  private String id;

  private String name;

  private Status status;

  private ArrayList<CardList> cardLists;

  @PersistenceConstructor
  public BoardView(String id, String name, Status status, ArrayList<CardList> cardLists) {
    this.id = id;
    this.name = name;
    this.status = status;
    this.cardLists = cardLists;
  }

  public static BoardView of(String id, String name, Status status) {

    return new BoardView(id, name, status, new ArrayList<>());
  }

  public static BoardView of(String id, String name, Status status, ArrayList<CardList> cardLists) {

    return new BoardView(id, name, status, cardLists);
  }

}
