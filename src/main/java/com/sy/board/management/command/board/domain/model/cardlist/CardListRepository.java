package com.sy.board.management.command.board.domain.model.cardlist;

import com.sy.board.management.core.domain.model.Error;
import io.vavr.control.Either;
import io.vavr.control.Option;

public interface CardListRepository {

  Either<Error, Option<CardList>> findById(CardListId cardListId);

  Either<Error, CardList> getByIdOrErrorOut(CardListId cardListId);

  Either<Error, CardList> save(CardList board);

}
