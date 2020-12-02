package com.sy.board.management.command.board.port.outgoing.adapter.repository;

import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static java.lang.String.format;

import com.sy.board.management.command.board.domain.model.Board;
import com.sy.board.management.command.board.domain.model.BoardId;
import com.sy.board.management.command.board.domain.model.BoardRepository;
import com.sy.board.management.core.domain.model.Error;
import com.sy.board.management.core.port.outgoing.adapter.eventstore.EventStore;
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
                           domainEvents -> right(Board.fromHistory(boardId, domainEvents.toJavaList())));
  }

  @Override
  public Either<Error, Board> getByIdOrErrorOut(BoardId boardId) {

    return findById(boardId).filterOrElse(aBoardOption -> !aBoardOption.isEmpty(),
                                          notFound -> Error.of(format("Board with id %s does not "
                                                                          + "exist.",
                                                                      boardId.getId())))
                            .map(aCardListOption -> aCardListOption.get());
  }

  @Override
  public Either<Error, Board> save(Board board) {

    var isItErroneous = board.getUncommittedChanges()
                             .stream()
                             .map(event -> eventStore
                                 .appendToStream(board.getBoardId().getId(), event))
                             .filter(r -> r.isLeft())
                             .findFirst();

    if (isItErroneous.isEmpty()) {
      LOG.info("Board with id {} successfully saved in event store", board.getBoardId().getId());
      board.markUnCommittedChangesAsCommitted();
      return right(board);
    }

    isItErroneous.get()
                 .peekLeft(error -> LOG.error("Could not save board with id {} in event "
                                                  + "store because of the following",
                                              board.getBoardId().getId()));
    return left(isItErroneous.get().getLeft());
  }
}
