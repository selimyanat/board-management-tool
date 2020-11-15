package com.sy.ticketingsystem.command.board.usecase;

import static com.sy.ticketingsystem.core.domain.model.Error.of;
import static java.lang.String.format;

import com.sy.ticketingsystem.command.board.domain.model.Board;
import com.sy.ticketingsystem.command.board.domain.model.BoardId;
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
                          .flatMap(errorOrBoard -> errorOrBoard)
                          .peek(boards -> LOG.info("Successfully archived board with id  {}",
                                                   command.getBoardId().toString()))
                          .peekLeft(error -> LOG.error("Could not archive the board with id {} "
                                                           + "because of the following {}",
                                                       command.getBoardId().toString(),
                                                       error.getMessage()));
  }
}
