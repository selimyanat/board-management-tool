package com.sy.board.management.command.board.domain.model.cardlist;

import static io.vavr.control.Either.right;
import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

import com.sy.board.management.command.board.domain.model.BoardId;
import com.sy.board.management.core.domain.model.Aggregate;
import com.sy.board.management.core.domain.model.DomainEvent;
import com.sy.board.management.core.domain.model.Error;
import com.sy.board.management.core.domain.model.EventSourceEntity;
import io.vavr.control.Either;
import io.vavr.control.Option;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Aggregate
@Setter(AccessLevel.PACKAGE)
@Getter
@ToString(callSuper = true)
public class CardList  extends EventSourceEntity {

  public enum Status { ACTIVE, ARCHIVED };

  private final CardListId cardListId;

  private String name;

  private Status status;

  private BoardId boardId;

  private List<Card> cards;

  private CardList(CardListId cardListId) {

    super();
    this.cardListId = cardListId;
    this.cards = new ArrayList<>();
  }

  private CardList(CardListId cardListId, List<DomainEvent> history) {

    this(cardListId);
    history.forEach(domainEvent -> apply(domainEvent, false));
  }

  public static Option<CardList> fromHistory(CardListId cardListId, List<DomainEvent> history) {

    if (history.isEmpty())
      return none();

    return some(new CardList(cardListId, history));
  }

  public static Either<Error, CardList> create(BoardId boardId, String name) {

    var cardList = new CardList(CardListId.newCardListId());
    var transition = CardListCreatedEvent.of(cardList.cardListId, boardId, name);
    cardList.apply(transition, true);
    return right(cardList);
  }

  public Either<Error, Card> createCard(String name, String description) {

    var card = Card.of(CardId.newCardId(), this.cardListId, name, description);
    var transition = CardCreatedEvent.of(card.getCardId(), cardListId, boardId, name, description);
    this.apply(transition, true);
    return right(card);
  }

}
