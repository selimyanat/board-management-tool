package com.sy.ticketingsystem.command.infrastructure.repository;

import static com.sy.ticketingsystem.core.domain.model.Unit.unit;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

import com.sy.ticketingsystem.command.domain.model.board.Board;
import com.sy.ticketingsystem.command.domain.model.board.BoardId;
import com.sy.ticketingsystem.command.domain.model.board.BoardRepository;
import com.sy.ticketingsystem.core.domain.model.DomainEvent;
import com.sy.ticketingsystem.core.domain.model.Error;
import com.sy.ticketingsystem.core.domain.model.Unit;
import com.sy.ticketingsystem.core.event.EventStore;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class EventStoreBoardRepository implements BoardRepository {

  private final EventStore eventStore;

  @Override
  public Either<Error, Option<Board>> findById(BoardId boardId) {

    return eventStore.readFromStream(boardId.getId())
                     .fold(Either::left,
                           domainEvents -> right(Board.fromHistory(domainEvents)));
  }

  @Override
  public Either<Error, Board> save(Board board) {

    var result = board.getUncommittedChanges()
                      .map(event -> append(board.getBoardId().getId(), event))
                      .find(r -> r.isLeft())
                      .getOrElse(() -> right(unit()));

    // TODO shall we publish here the uncommitted event ??

    return result.isRight() ?
        right(board.markUnCommittedChangesAsCommitted()) :
        left(result.getLeft());

  }

  private Either<Error, Unit> append(String streamId, DomainEvent event) {

    return eventStore.appendToStream(streamId, event);
  }
}
