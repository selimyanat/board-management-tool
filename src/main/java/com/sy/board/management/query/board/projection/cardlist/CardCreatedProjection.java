package com.sy.board.management.query.board.projection.cardlist;

import static io.vavr.control.Option.some;

import com.sy.board.management.command.board.domain.model.cardlist.CardCreatedEvent;
import com.sy.board.management.command.board.domain.model.cardlist.CardListId;
import com.sy.board.management.core.query.projection.Projection;
import com.sy.board.management.query.board.domain.model.BoardView;
import com.sy.board.management.query.board.domain.model.Card;
import com.sy.board.management.query.board.domain.model.CardList;
import com.sy.board.management.query.board.port.outgoing.adapter.BoardViewRepository;
import io.vavr.Function2;
import io.vavr.control.Option;
import java.util.ArrayList;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class CardCreatedProjection implements Projection<CardCreatedEvent> {

  private final BoardViewRepository boardViewRepository;

  @Override
  public void on(CardCreatedEvent cardCreatedEvent) {

    var executionContext = new ExecutionContext();
    executionContext.event = cardCreatedEvent;

    var boardViewOption = boardViewRepository.findById(executionContext.event.getBoardId().getId());

    Option.ofOptional(boardViewOption)
          .onEmpty(() -> LOG.error("Board view with id {} does not exit.",
                                   executionContext.event.getBoardId().getId()
          ))
          .map(boardView -> executionContext.setBoardViews(some(boardView)))
          .map(ctx -> findCardListById().apply(executionContext.boardViews.get().getCardLists(),
                                               executionContext.event.getCardListId()
          ))
          .flatMap(cardListOption -> cardListOption)
          .map(cardList -> executionContext.setCardList(some(cardList)))
          .map(ctx -> toCard().apply(executionContext.event))
          .map(card -> executionContext.setCard(some(card)))
          .map(ctx -> ctx.cardList.get().getCards().add(ctx.card.get()))
          .toTry()
          .map(bool -> boardViewRepository.save(executionContext.boardViews.get()))
          .onFailure(throwable -> LOG.error("The card with id {} cannot be added to the card list "
                                                + "with id {} because of the following error  ",
                                            executionContext.event.getCardId().getId(),
                                            executionContext.event.getCardListId().getId(),
                                            throwable
          ))
          .onSuccess(boardView -> LOG.info("Card with id {} and name {} has been "
                                                  + "successfully added to card list with "
                                                  + "id {} and name {}",
                                              executionContext.card.get().getId(),
                                              executionContext.card.get().getName(),
                                              executionContext.cardList.get().getId(),
                                              executionContext.cardList.get().getName()
          ));
  }

  Function2<ArrayList<CardList>, CardListId, Option<CardList>> findCardListById() {

    return (cardLists, cardListId) -> Option.ofOptional(cardLists.stream()
                                                                 .filter(
                                                                     cardList -> cardList.getId()
                                                                                         .equals(
                                                                                             cardListId
                                                                                                 .getId()))
                                                                 .findFirst())
                                            .onEmpty(() -> LOG
                                                .error("Card list with id {} does not exit",
                                                       cardListId.getId()
                                                ));
  }

  Function<CardCreatedEvent, Card> toCard() {

    return cardCreatedEvent -> Card.of(cardCreatedEvent.getCardId().getId(),
                                       cardCreatedEvent.getName(),
                                       cardCreatedEvent.getDescription()
    );
  }

  @Setter
  private static class ExecutionContext {

    CardCreatedEvent event;
    Option<BoardView> boardViews;
    Option<CardList> cardList;
    Option<Card> card;
  }
}
