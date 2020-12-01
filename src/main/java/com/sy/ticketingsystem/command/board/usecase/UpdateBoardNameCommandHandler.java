package com.sy.ticketingsystem.command.board.usecase;

import static io.vavr.control.Option.some;

import com.sy.ticketingsystem.command.board.domain.model.Board;
import com.sy.ticketingsystem.command.board.domain.model.BoardId;
import com.sy.ticketingsystem.command.board.domain.model.BoardRepository;
import com.sy.ticketingsystem.core.command.CommandHandler;
import com.sy.ticketingsystem.core.domain.model.Error;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class UpdateBoardNameCommandHandler implements CommandHandler<UpdateBoardNameCommand, Board> {

  private final BoardRepository boardRepository;

  @Override
  public Either<Error, Board> handle(UpdateBoardNameCommand command) {

    var executionContext = new ExecutionContext();
    executionContext.command = command;

    return boardRepository.getByIdOrErrorOut(BoardId.fromExisting(command.getBoardId().toString()))
                          .map(aBoard -> executionContext.setBoard(some(aBoard)))
                          .map(ctx -> ctx.board.get().updateName(ctx.command.getNewName()))
                          .flatMap(boardOrError -> boardOrError)
                          .map(aBoard -> boardRepository.save(executionContext.board.get()))
                          .flatMap(errorOrBoard -> errorOrBoard)
                          .peek(board -> LOG.info("Successfully updated board with id {} with "
                                                      + "name {}",
                                                  executionContext.board.get().getBoardId().getId(),
                                                  executionContext.board.get().getName()))
                          .peekLeft(error -> LOG.error("Could not create the board with id {} because of the following {}",
                                                       command.getBoardId(),
                                                       error.getMessage()));
  }

  @Setter
  private static class ExecutionContext {

    UpdateBoardNameCommand command;
    Option<Board> board;
  }
}
