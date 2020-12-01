package com.sy.ticketingsystem.command.board.port.incoming.adapter.resources;

import static java.util.UUID.randomUUID;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.created;

import com.sy.ticketingsystem.command.board.usecase.CreateCardListCommand;
import com.sy.ticketingsystem.core.command.CommandBus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@Api(tags = {"Commands"})
@AllArgsConstructor
public class CardListController {

  public static final String CARD_LISTS_COMMAND_ROOT_URL = "/commands/cardLists/";

  private final CommandBus commandBus;

  @PostMapping(CARD_LISTS_COMMAND_ROOT_URL)
  @ApiOperation(value = "Create a card list")
  public ResponseEntity<?> createCardList(@RequestBody CreateCardListRequest request) {

    var command = CreateCardListCommand.of(request.getBoardId(), request.getName());
    return commandBus.accept(command)
                     .fold(error -> badRequest().body(error.getMessage()),
                           cardList -> created(UriComponentsBuilder.fromPath("cardLists/{id}")
                                                                   .build(cardList.getCardListId().getId())).build());
  }


}
