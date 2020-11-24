package com.sy.ticketingsystem.query.board.projection.cardlist;

import static io.vavr.control.Option.some;

import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardListCreatedEvent;
import com.sy.ticketingsystem.core.domain.model.Error;
import com.sy.ticketingsystem.core.query.projection.Projection;
import com.sy.ticketingsystem.query.board.domain.model.BoardView;
import com.sy.ticketingsystem.query.board.domain.model.CardList;
import com.sy.ticketingsystem.query.board.domain.model.CardList.Status;
import com.sy.ticketingsystem.query.board.port.outgoing.adapter.BoardViewRepository;
import io.vavr.control.Option;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class CardListCreatedProjection implements Projection<CardListCreatedEvent> {

  private final BoardViewRepository boardViewRepository;

  @Override
  public void on(CardListCreatedEvent cardListCreatedEvent) {

    var executionContext = new ExecutionContext();
    executionContext.event = cardListCreatedEvent;

    var boardViewOption = boardViewRepository.findById(cardListCreatedEvent.getBoardId().getId());

    Option.ofOptional(boardViewOption)
          .onEmpty(() -> LOG.error("Board view with id {} does not exit",
                                   executionContext.event.getBoardId().getId()))
          .map(boardView -> executionContext.setBoardViews(some(boardView)))
          .map(ctx -> toCardList().apply(executionContext.event))
          .map(cardList -> executionContext.setCardList(Option.some(cardList)))
          .map(ctx -> ctx.boardViews.get().getCardLists().add(ctx.cardList.get()))
          .toTry()
          .map(bool -> boardViewRepository.save(executionContext.boardViews.get()))
          .onFailure(throwable -> LOG.error("The card list with id {} cannot be added to the "
                                                + "board view with id {} because of the following error ",
                                            executionContext.event.getCardListId().getId(),
                                            executionContext.event.getBoardId().getId(),
                                            throwable))
          .onSuccess(boardView -> LOG.info("Card list with id {} and name {} has been successfully "
                                               + "added to board view with id {} and name {}",
                                           executionContext.cardList.get().getId(),
                                           executionContext.cardList.get().getName(),
                                           executionContext.boardViews.get().getId(),
                                           executionContext.boardViews.get().getName()));
  }

  Function<CardListCreatedEvent, CardList> toCardList() {

    return cardListCreatedEvent ->  CardList.of(cardListCreatedEvent.getCardListId().getId(),
                                            cardListCreatedEvent.getName(),
                                            Status.ACTIVE);
  }

  @Setter
  private static class ExecutionContext {

    CardListCreatedEvent event;
    Option<BoardView> boardViews;
    Option<CardList> cardList;
  }
}
