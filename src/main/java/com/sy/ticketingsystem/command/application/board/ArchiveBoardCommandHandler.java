package com.sy.ticketingsystem.command.application.board;

import static com.sy.ticketingsystem.command.domain.board.Board.fromHistory;
import static com.sy.ticketingsystem.core.Error.of;

import com.sy.ticketingsystem.command.domain.board.Board;
import com.sy.ticketingsystem.core.CommandHandler;
import com.sy.ticketingsystem.core.Error;
import com.sy.ticketingsystem.core.EventStore;
import com.sy.ticketingsystem.core.EventStore.InMemoryEventStore;
import io.vavr.Tuple;
import io.vavr.control.Either;
import java.util.UUID;

public class ArchiveBoardCommandHandler extends CommandHandler<ArchiveBoardCommand> {

  private final EventStore eventStore;

  public ArchiveBoardCommandHandler(EventStore eventStore) {

    this.eventStore = eventStore;
  }


  @Override
  public Either<Error, Board> handle(ArchiveBoardCommand command) {

    // TODO Do we keep validation as a return type ?

    var theBoard = eventStore.readFromStream(command.id)
                             .map(domainEvents -> fromHistory(domainEvents));

    if(theBoard.isLeft())
      return Either.left(theBoard.getLeft());

    if (theBoard.isEmpty())
      return Either.left(of("Board does not exist"));

    var compute = theBoard.get()
                         .get()
                         .archive()
                         .map(theTransition -> Tuple.of(theTransition,
                                                        theBoard.get().get().apply(theTransition)))
                         .map(boardTuple1 -> eventStore.appendToStream(boardTuple1._2.id,
                                                                       boardTuple1._2.version,
                                                                       boardTuple1._1)
                                                       .map(aBoolean -> boardTuple1._2));

    return compute.flatMap(board -> board);
  }

  public static void main(String[] args) {

    var handler = new ArchiveBoardCommandHandler(new InMemoryEventStore());

    var uuid = UUID.randomUUID();
    var createBoard = CreateBoardCommand.builder().id(uuid).name("test");
    var updateBoardName = UpdateBoardNameCommand.builder().id(uuid).newName("test-2");
    var archiveBoard = ArchiveBoardCommand.builder().id(uuid);
  }

}
