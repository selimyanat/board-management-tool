package com.sy.ticketingsystem.command.board.port.outgoing.adapter.repository;


import static com.sy.ticketingsystem.core.domain.model.Unit.unit;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static java.lang.String.format;

import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardList;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardListId;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardListRepository;
import com.sy.ticketingsystem.core.domain.model.Error;
import com.sy.ticketingsystem.core.port.outgoing.adapter.eventstore.EventStore;
import io.vavr.control.Either;
import io.vavr.control.Option;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
@Slf4j
public class EventStoreCardListRepository implements CardListRepository {

  private final EventStore eventStore;

  @Override
  public Either<Error, Option<CardList>> findById(CardListId cardListId) {

    return eventStore.readFromStream(cardListId.getId())
                     .peekLeft(error -> LOG.error("Could not read the card list with id {} from "
                                                      + "event store because of the following {}",
                                                  cardListId.getId(),
                                                  error.getMessage()))
                     .peek(domainEvents -> LOG.debug("Successfully read card list with id {} "
                                                         + "from event store",
                                                     cardListId.getId()))
                     .fold(Either::left,
                           domainEvents -> right(CardList.fromHistory(cardListId, domainEvents.toJavaList())));
  }

  @Override
  public Either<Error, CardList> getByIdOrErrorOut(CardListId cardListId) {

    return findById(cardListId).filterOrElse(aCardOption -> !aCardOption.isEmpty(),
                                             notFound -> Error.of(format("Card list with id %s does "
                                                                          + "not "
                                                                          + "exist.",
                                                                         cardListId.getId())))
                               .map(aCardListOption -> aCardListOption.get());
  }

  @Override
  public Either<Error, CardList> save(CardList cardList) {

    var isItErroneous = cardList.getUncommittedChanges()
                                .stream()
                                .map(event -> eventStore.appendToStream(cardList.getCardListId().getId(), event))
                                .filter(r -> r.isLeft())
                                .findFirst();

    if (isItErroneous.isEmpty()) {
      LOG.info("Card list with id {} successfully saved in event store", cardList.getCardListId().getId());
      cardList.markUnCommittedChangesAsCommitted();
      return right(cardList);
    }

    isItErroneous.get()
                 .peekLeft(error -> LOG.error("Could not save card list with id {} in event "
                                                  + "store because of the following",
                                              cardList.getCardListId().getId()));
    return left(isItErroneous.get().getLeft());
  }
}
