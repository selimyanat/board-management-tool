package com.sy.ticketingsystem.command.board.usecase;

import com.sy.ticketingsystem.command.board.domain.model.cardlist.Card;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardList;
import com.sy.ticketingsystem.command.board.domain.model.cardlist.CardListId;
import com.sy.ticketingsystem.core.command.Command;
import java.util.UUID;
import lombok.Value;

@Value(staticConstructor = "of")
public class CreateCardCommand implements Command<Card> {

  private UUID cardListId;

  private String name;

  private String description;

}
