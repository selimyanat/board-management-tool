package com.sy.board.management.command.board.domain.model;

import com.sy.board.management.core.domain.model.Error;
import io.vavr.control.Either;
import io.vavr.control.Option;

public interface BoardRepository {

  Either<Error, Option<Board>> findById(BoardId boardId);

  Either<Error, Board> getByIdOrErrorOut(BoardId boardId);

  Either<Error, Board> save(Board board);

}
