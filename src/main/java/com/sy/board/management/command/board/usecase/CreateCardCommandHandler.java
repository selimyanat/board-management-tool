package com.sy.board.management.command.board.usecase;

import static io.vavr.control.Option.some;

import com.sy.board.management.command.board.domain.model.cardlist.CardList;
import com.sy.board.management.command.board.domain.model.cardlist.CardListId;
import com.sy.board.management.command.board.domain.model.cardlist.CardListRepository;
import com.sy.board.management.command.board.domain.model.Board;
import com.sy.board.management.command.board.domain.model.BoardRepository;
import com.sy.board.management.command.board.domain.model.cardlist.Card;
import com.sy.board.management.core.command.CommandHandler;
import com.sy.board.management.core.domain.model.Error;
import io.vavr.control.Either;
import io.vavr.control.Option;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class CreateCardCommandHandler implements CommandHandler<CreateCardCommand, Card> {

  private final BoardRepository boardRepository;

  private final CardListRepository cardListRepository;

  @Override
  public Either<Error, Card> handle(CreateCardCommand command) {

    var executionContext = new ExecutionContext();
    executionContext.command = command;

    return cardListRepository
        .getByIdOrErrorOut(CardListId.fromExisting(command.getCardListId().toString()))
        .map(cardList -> executionContext.setCardList(some(cardList)))
        .map(ctx -> boardRepository.getByIdOrErrorOut(ctx.cardList.get().getBoardId()))
        .flatMap(boardOrError -> boardOrError)
        .map(board -> executionContext.setBoard(some(board)))
        .map(createCardOrError())
        .flatMap(tupleOfCardListAndCardOrdError -> tupleOfCardListAndCardOrdError)
        .map(card -> executionContext.setCard(some(card)))
        .map(saveCardListOrError())
        .flatMap(cardListOrError -> cardListOrError)
        .map(cardList -> executionContext.card.get())
        .peek(card -> LOG.info("Card with id {} has been successfully created",
                               executionContext.card.get().getCardId().getId()))
        .peekLeft(error -> LOG.error("Could not create card with name {} "
                                         + "because of the following {}",
                                     command.getName(),
                                     error.getMessage()
        ));

  }

  Function<ExecutionContext, Either<Error, Card>> createCardOrError() {

    return ctx -> ctx.board.get().createCard(ctx.cardList.get(), ctx.command.getName(),
                                             ctx.command.getDescription()
    );
  }

  Function<ExecutionContext, Either<Error, CardList>> saveCardListOrError() {

    return ctx -> cardListRepository.save(ctx.cardList.get());
  }

  @Setter
  private static class ExecutionContext {

    CreateCardCommand command;
    Option<CardList> cardList;
    Option<Board> board;
    Option<Card> card;
  }
}
