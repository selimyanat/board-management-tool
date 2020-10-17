package com.sy.ticketingsystem.command.domain.board;

import static io.vavr.API.$;
import static io.vavr.API.Case;
import static io.vavr.API.Match;
import static io.vavr.collection.List.empty;
import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

import com.sy.ticketingsystem.core.DomainEvent;
import com.sy.ticketingsystem.core.Error;
import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;
import java.util.UUID;
import lombok.Builder;

@Builder
public class Board {

  public UUID id;

  public String name;

  public enum Status  { ACTIVE, ARCHIVED };

  public Status status;

  public int version;

  public static Board uninitialized() {
    return Board.builder().build();
  }

  public Board apply(DomainEvent <Board> domainEvent) {
    return  domainEvent.rehydrate(this);
  }

  public Either<Error, DomainEvent<Board>> create(String name) {
    // TODO Put the business and validation rules to transition state
    return Either.right(BoardCreatedEvent.builder()
                                         .id(UUID.randomUUID())
                                         .name(name)
                                         .status(Status.ACTIVE)
                                         .build());
  }

  public Either<Error, DomainEvent<Board>> updateName(String newName) {
    // TODO Put the business and validation rules to transition state
    return Either.right(BoardNameUpdatedEvent.builder()
                                         .newName(name)
                                         .build());
  }

  public Either<Error, DomainEvent<Board>> archive() {
    // TODO Put the business and validation rule to transition to the next state
    return Either.right(BoardArchivedEvent.builder()
                                          .status(Status.ARCHIVED)
                                          .build());
  }


  public static Option<Board> fromHistory(List<DomainEvent> history) {

    return Match(history).of(
        Case($(empty()), none()),
        Case($(), some(history.foldLeft(uninitialized(), (board, boardDomainEvent) -> board.apply(boardDomainEvent))))
    );
  }

}
