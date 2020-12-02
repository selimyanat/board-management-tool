package com.sy.board.management.command.board.usecase;

import com.sy.board.management.command.board.domain.model.cardlist.Card;
import com.sy.board.management.core.command.Command;
import java.util.UUID;
import lombok.Value;

@Value(staticConstructor = "of")
public class CreateCardCommand implements Command<Card> {

  private UUID cardListId;

  private String name;

  private String description;

}
