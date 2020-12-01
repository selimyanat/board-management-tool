package com.sy.ticketingsystem.command.board.domain.model;

import static com.sy.ticketingsystem.core.domain.model.Unit.unit;
import static io.vavr.control.Either.left;
import static io.vavr.control.Either.right;
import static io.vavr.control.Option.none;
import static io.vavr.control.Option.some;

import com.sy.ticketingsystem.command.board.domain.model.cardlist.Card;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardList;
import com.sy.ticketingsystem.core.domain.model.AggregateRoot;
import com.sy.ticketingsystem.core.domain.model.DomainEvent;
import com.sy.ticketingsystem.core.domain.model.Error;
import com.sy.ticketingsystem.core.domain.model.EventSourceEntity;
import com.sy.ticketingsystem.core.domain.model.Unit;
import io.vavr.control.Either;
import io.vavr.control.Option;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AggregateRoot
@Setter(AccessLevel.PACKAGE)
@Getter
@ToString(callSuper = true)
public class Board extends EventSourceEntity {

  public enum Status { ACTIVE, ARCHIVED };

  private final BoardId boardId;

  private String name;

  private Status status;

  private List<BoardCardList> boardCardLists;

  private Board(BoardId boardId) {

    super();
    this.boardId = boardId;
    this.boardCardLists = new ArrayList<>();
  }

  private Board(BoardId boardId, List<DomainEvent> history) {

    this(boardId);
    history.forEach(domainEvent -> apply(domainEvent, false));
  }

  public static Option<Board> fromHistory(BoardId boardId, List<DomainEvent> history) {

    if (history.isEmpty())
     return none();

    return some(new Board(boardId, history));
  }

  public static Either<Error, Board> create(String name) {

    var board = new Board(BoardId.newBoardId());
    var transition = BoardCreated.of(board.boardId, name);
    board.apply(transition, true);
    return right(board);
  }

  public Either<Error, Unit> updateName(String newName) {

    var transition =  BoardNameUpdated.of(this.boardId,this.name, newName);
    apply(transition, true);
    return right(unit());
  }

  public Either<Error, Unit> archive() {

    var transition = BoardArchived.of(this.boardId, this.status);
    apply(transition, true);
    return right(unit());
  }

  public Either<Error, CardList> addCardList(String name) {

    var cardList = CardList.create(this.boardId, name);

    if (cardList.isLeft())
      return left(cardList.getLeft());

    var ordering = this.boardCardLists.size() + 1;
    var transition = BoardCardListCreated.of(
        cardList.get().getCardListId(),
        ordering);
    apply(transition, true);
    return right(cardList.get());
  }

  public Either<Error, Card> createCard(CardList cardList, String name, String description) {

    return cardList.createCard(name, description);
  }
}