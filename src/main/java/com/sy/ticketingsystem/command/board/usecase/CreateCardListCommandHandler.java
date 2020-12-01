package com.sy.ticketingsystem.command.board.usecase;

import static io.vavr.control.Option.some;

import com.sy.ticketingsystem.command.board.domain.model.Board;
import com.sy.ticketingsystem.command.board.domain.model.BoardId;
import com.sy.ticketingsystem.command.board.domain.model.BoardRepository;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardList;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardListRepository;
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
public class CreateCardListCommandHandler implements CommandHandler<CreateCardListCommand, CardList> {

  private final BoardRepository boardRepository;

  private final CardListRepository cardListRepository;

  @Override
  public Either<Error, CardList> handle(CreateCardListCommand command) {

    var executionContext = new ExecutionContext();
    executionContext.command = command;

    return boardRepository.getByIdOrErrorOut(BoardId.fromExisting(command.getBoardId().toString()))
                          .map(board -> executionContext.setBoard(some(board)))
                          .map(ctx -> ctx.board.get().addCardList(command.getName()))
                          .flatMap(tupleOfBoardAndCardListOrdError -> tupleOfBoardAndCardListOrdError)
                          .map(cardList -> executionContext.setCardList(some(cardList)))
                          .map(ctx -> boardRepository.save(ctx.board.get()))
                          .flatMap(boardOrError -> boardOrError)
                          .map(aBoard -> cardListRepository.save(executionContext.cardList.get()))
                          .flatMap(cardListOrError -> cardListOrError)
                          .peek(cardList -> LOG.info("Card list with id {} and name {} successfully added to "
                                              + "board with id {}",
                                                     executionContext.cardList.get().getCardListId().getId(),
                                                     executionContext.cardList.get().getName(),
                                                     executionContext.board.get().getBoardId().getId()))
                          .peekLeft(error -> LOG.error("Could not add card list with name {} to "
                                                           + "the board with id {} because of the"
                                                           + " following {}",
                                                       command.getName(),
                                                       command.getBoardId().toString(),
                                                       error.getMessage()
                          ));
  }

  @Setter
  private static class ExecutionContext {

    CreateCardListCommand command;
    Option<CardList> cardList;
    Option<Board> board;
  }
}
