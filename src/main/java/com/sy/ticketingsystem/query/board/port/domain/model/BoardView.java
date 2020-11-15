package com.sy.ticketingsystem.query.board.port.domain.model;

import java.util.UUID;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "board_view")
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString(callSuper = true)
public class BoardView {

  public enum Status { ACTIVE, ARCHIVED };

  @Id
  private UUID boardId;

  private String name;

  private Status status;


}
