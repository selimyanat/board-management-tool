package com.sy.ticketingsystem.command.domain.model.board;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.collection.List.empty;
import static io.vavr.control.Either.right;
import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

import com.sy.ticketingsystem.core.domain.model.DomainEvent;
import com.sy.ticketingsystem.core.domain.model.Error;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class Board {

  public enum Status { ACTIVE, ARCHIVED };

  private BoardId boardId;

  private String name;

  private Status status;

  private List<DomainEvent> committedChanges;

  private List<DomainEvent> uncommittedChanges;

  public static Board uninitialized() {
    return Board.builder().build();
  }

  public static Option<Board> fromHistory(List<DomainEvent> history) {

    return Match(history).of(
        Case($(empty()), none()),
        Case($(), some(history.foldLeft(uninitialized(),
                                        (board, boardDomainEvent) -> board.apply(boardDomainEvent, false)
        )))
    );
  }

  public static Either<Error, Board> create(String name) {

    var transition = BoardCreated.newInstance(BoardId.newBoardId(), name);
    return right(uninitialized().apply(transition));
  }

  public Either<Error, Board> updateName(String newName) {

    var transition =  BoardNameUpdated.newInstance(newName);
    return right(this.apply(transition));
  }

  public Either<Error, Board> archive() {

    var transition = BoardArchived.newInstance();
    return right(this.apply(transition));
  }

  public Board apply(DomainEvent <Board> domainEvent) {

    return apply(domainEvent, true);
  }

  public Board markUnCommittedChangesAsCommitted() {

    this.committedChanges = this.committedChanges.appendAll(this.uncommittedChanges);
    this.uncommittedChanges = this.uncommittedChanges.removeAll(this.uncommittedChanges);
    return this;
  }

  private Board apply(DomainEvent <Board> domainEvent, boolean newEvent) {

    var newState = domainEvent.rehydrate(this);

    if(newEvent)
      newState.uncommittedChanges = newState.uncommittedChanges.append(domainEvent);

    if(!newEvent)
      newState.committedChanges = newState.committedChanges.append(domainEvent);

    return newState;
  }
}