package com.sy.ticketingsystem.command.application.board;

import com.sy.ticketingsystem.command.domain.model.board.Board;
import com.sy.ticketingsystem.command.domain.model.board.BoardRepository;
import com.sy.ticketingsystem.core.application.CommandHandler;
import com.sy.ticketingsystem.core.domain.model.Error;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CreateBoardCommandHandler implements CommandHandler<CreateBoardCommand, Board> {

  private final BoardRepository boardRepository;

  @Override
  public Either<Error, Board> handle(CreateBoardCommand command) {

    return Board.create(command.getName())
                .map(board -> boardRepository.save(board))
                .flatMap(errorOrBoard -> errorOrBoard);

  }
}
