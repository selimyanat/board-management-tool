package com.sy.ticketingsystem.command.domain.model.board;

import com.sy.ticketingsystem.core.domain.model.Error;
import io.vavr.control.Either;
import io.vavr.control.Option;

public interface BoardRepository {

  Either<Error, Option<Board>> findById(BoardId boardId);

  Either<Error, Board> save(Board board);

}
