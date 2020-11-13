package com.sy.ticketingsystem.command.application.board;

import static com.sy.ticketingsystem.core.domain.model.Error.of;
import static java.lang.String.format;

import com.sy.ticketingsystem.command.domain.model.board.Board;
import com.sy.ticketingsystem.command.domain.model.board.BoardId;
import com.sy.ticketingsystem.command.domain.model.board.BoardRepository;
import com.sy.ticketingsystem.core.application.CommandHandler;
import com.sy.ticketingsystem.core.domain.model.Error;
import io.vavr.control.Either;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ArchiveBoardCommandHandler implements CommandHandler<ArchiveBoardCommand, Board>  {

  private final BoardRepository boardRepository;

  @Override
  public Either<Error, Board> handle(ArchiveBoardCommand command) {

    return boardRepository.findById(BoardId.fromExisting(command.getBoardId().toString()))
                          .filterOrElse(theBoard -> !theBoard.isEmpty(),
                                        theBoard -> of((format(
                                            "Board with id %s does not exist. The board cannot be archived."
                                            , command.getBoardId().toString())))
                          )
                          .map(theBoard -> theBoard.get())
                          .map(theBoard -> theBoard.archive())
                          .flatMap(theBoard -> theBoard)
                          .map(theBoard -> boardRepository.save(theBoard))
                          .flatMap(errorOrBoard -> errorOrBoard);
  }
}
