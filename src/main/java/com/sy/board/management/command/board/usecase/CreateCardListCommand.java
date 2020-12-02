package com.sy.board.management.command.board.usecase;

import com.sy.board.management.command.board.domain.model.cardlist.CardList;
import com.sy.board.management.core.command.Command;
import java.util.UUID;
import lombok.Value;

@Value(staticConstructor = "of")
public class CreateCardListCommand implements Command<CardList> {

  private UUID boardId;

  private String name;

}
