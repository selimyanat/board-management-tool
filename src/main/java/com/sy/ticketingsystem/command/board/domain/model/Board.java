package com.sy.ticketingsystem.command.board.domain.model;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.collection.List.empty;
import static io.vavr.control.Either.right;
import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

import com.sy.ticketingsystem.core.domain.model.DomainEvent;
import com.sy.ticketingsystem.core.domain.model.Error;
import com.sy.ticketingsystem.core.domain.model.EventSourceEntity;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter(AccessLevel.PACKAGE)
@Getter
@ToString(callSuper = true)
public class Board extends EventSourceEntity {

  public enum Status { ACTIVE, ARCHIVED };

  private BoardId boardId;

  private String name;

  private Status status;

  private Board(BoardId boardId) {

    this.boardId = boardId;
  }

  private Board(BoardId boardId, List<DomainEvent> history) {

    this.boardId = boardId;
    history.forEach(domainEvent -> apply(domainEvent, false));
  }

  public static Option<Board> fromHistory(BoardId boardId, List<DomainEvent> history) {

    return Match(history).of(
        Case($(empty()), none()),
        Case($(), some(new Board(boardId, history)))
    );
  }

  public static Either<Error, Board> create(String name) {

    var board = new Board(BoardId.newBoardId());
    var transition = BoardCreated.newInstance(board.boardId, name);
    board.apply(transition, true);
    return right(board);
  }

  public Either<Error, Board> updateName(String newName) {

    var transition =  BoardNameUpdated.newInstance(this.name, newName);
    apply(transition, true);
    return right(this);
  }

  public Either<Error, Board> archive() {

    var transition = BoardArchived.newInstance();
    apply(transition, true);
    return right(this);
  }
}