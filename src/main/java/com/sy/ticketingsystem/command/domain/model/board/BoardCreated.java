package com.sy.ticketingsystem.command.domain.model.board;

import com.sy.ticketingsystem.command.domain.model.board.Board.Status;
import com.sy.ticketingsystem.core.domain.model.DomainEvent;
import io.vavr.collection.List;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class BoardCreated extends DomainEvent <Board> {

  public static final String BOARD_CREATED = "BOARD_CREATED";

  private BoardId boardId;

  private String boardName;

  public BoardCreated(BoardId id, String boardName) {

    super();
    this.boardId = id;
    this.boardName = boardName;
  }

  public static BoardCreated newInstance(BoardId id, String name) {

    return new BoardCreated(id, name);
  }

  public Board rehydrate(Board board) {

    return board.toBuilder()
                .boardId(this.boardId)
                .name(this.boardName)
                .status(Status.ACTIVE)
                .committedChanges(List.empty())
                .uncommittedChanges(List.empty())
                .build();

  }

  @Override
  public String getEventName() {

    return BOARD_CREATED;
  }
}
