package com.sy.ticketingsystem.command.board.usecase;

import com.sy.ticketingsystem.command.board.domain.model.Board;
import com.sy.ticketingsystem.command.board.domain.model.BoardRepository;
import com.sy.ticketingsystem.core.command.CommandHandler;
import com.sy.ticketingsystem.core.domain.model.Error;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class CreateBoardCommandHandler implements CommandHandler<CreateBoardCommand, Board> {

  private final BoardRepository boardRepository;

  @Override
  public Either<Error, Board> handle(CreateBoardCommand command) {

    return Board.create(command.getName())
                .map(board -> boardRepository.save(board))
                .peekLeft(error -> LOG.error(""))
                .flatMap(errorOrBoard -> errorOrBoard)
                .peek(board -> LOG.info("A new board has been created with id {}", board.getBoardId()
                                                                                        .getId()))
                .peekLeft(error -> LOG.error("Could not create the board with id {} because of the following {}",
                                             command.getId().toString(),
                                             error.getMessage()));

  }
}
