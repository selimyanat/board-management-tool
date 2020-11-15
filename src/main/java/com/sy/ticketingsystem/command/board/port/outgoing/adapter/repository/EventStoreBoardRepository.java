package com.sy.ticketingsystem.command.board.port.outgoing.adapter.repository;

import static com.sy.ticketingsystem.core.domain.model.Unit.unit;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;

import com.sy.ticketingsystem.command.board.domain.model.Board;
import com.sy.ticketingsystem.command.board.domain.model.BoardId;
import com.sy.ticketingsystem.command.board.domain.model.BoardRepository;
import com.sy.ticketingsystem.core.domain.model.Error;
import com.sy.ticketingsystem.core.port.outgoing.adapter.eventstore.EventStore;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
@Slf4j
public class EventStoreBoardRepository implements BoardRepository {

  private final EventStore eventStore;

  @Override
  public Either<Error, Option<Board>> findById(BoardId boardId) {

    return eventStore.readFromStream(boardId.getId())
                     .peekLeft(error -> LOG.error("Could not read the board with id {} from event"
                         + " store because of the following {}",
                                                 boardId.getId(),
                                                 error.getMessage()))
                     .peek(domainEvents -> LOG.debug("Successfully read board with id {} "
                                                         + "from event store",
                                                     boardId.getId()))
                     .fold(Either::left,
                           domainEvents -> right(Board.fromHistory(boardId, domainEvents)));
  }

  @Override
  public Either<Error, Board> save(Board board) {

    var result = board.getUncommittedChanges()
                      .map(event -> eventStore.appendToStream(board.getBoardId().getId(), event))
                      .find(r -> r.isLeft())
                      .getOrElse(() -> right(unit()))
                      .peekLeft(error -> LOG.error("Could not save board with id {} in event "
                                                      + "store because of the following",
                                                  board.getBoardId().getId()));

    if (result.isRight()) {
      LOG.info("Board with id {} successfully saved in event store for board with id",
               board.getBoardId().getId());
      board.markUnCommittedChangesAsCommitted();
      return right(board);
    }

    return left(result.getLeft());
  }
}
