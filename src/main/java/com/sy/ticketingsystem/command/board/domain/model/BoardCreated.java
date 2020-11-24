package com.sy.ticketingsystem.command.board.domain.model;

import com.sy.ticketingsystem.command.board.domain.model.Board.Status;
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

    return (Board) board.setBoardId(this.boardId)
                        .setName(this.boardName)
                        .setStatus(Status.ACTIVE)
                        .setCommittedChanges(List.empty())
                        .setUncommittedChanges(List.empty());
  }

  @Override
  public String getEventName() {

    return BOARD_CREATED;
  }
}
