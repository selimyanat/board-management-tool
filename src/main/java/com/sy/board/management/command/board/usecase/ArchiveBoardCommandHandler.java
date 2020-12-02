package com.sy.board.management.command.board.usecase;

import static io.vavr.control.Option.some;

import com.sy.board.management.command.board.domain.model.Board;
import com.sy.board.management.command.board.domain.model.BoardId;
import com.sy.board.management.command.board.domain.model.BoardRepository;
import com.sy.board.management.core.command.CommandHandler;
import com.sy.board.management.core.domain.model.Error;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class ArchiveBoardCommandHandler implements CommandHandler<ArchiveBoardCommand, Board>  {

  private final BoardRepository boardRepository;

  @Override
  public Either<Error, Board> handle(ArchiveBoardCommand command) {

    var executionContext = new ExecutionContext();
    executionContext.command = command;

    return boardRepository.getByIdOrErrorOut(BoardId.fromExisting(command.getBoardId().toString()))
                          .map(aBoard -> executionContext.setBoard(some(aBoard)))
                          .map(ctx -> ctx.board.get().archive())
                          .flatMap(errorOrBoard -> errorOrBoard)
                          .map(aBoard -> boardRepository.save(executionContext.board.get()))
                          .flatMap(errorOrBoard -> errorOrBoard)
                          .peek(boards -> LOG.info("Successfully archived board with id  {}",
                                                   executionContext.board.get().getBoardId().toString()))
                          .peekLeft(error -> LOG.error("Could not archive the board with id {} "
                                                           + "because of the following {}",
                                                       command.getBoardId().toString(),
                                                       error.getMessage()));
  }


  @Setter
  private static class ExecutionContext {

    ArchiveBoardCommand command;
    Option<Board> board;
  }
}
