package com.sy.board.management.command.board.port.incoming.adapter.resources;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.created;

import com.sy.board.management.command.board.usecase.CreateCardCommand;
import com.sy.board.management.core.command.CommandBus;
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
public class CardController {

  public static final String CARD_COMMAND_ROOT_URL = "/commands/cards/";

  private final CommandBus commandBus;

  @PostMapping(CARD_COMMAND_ROOT_URL)
  @ApiOperation(value = "Create a card by returning its id in the http header")
  public ResponseEntity<?> createCard(@RequestBody CreateCardRequest request) {

    var command = CreateCardCommand.of(request.getCardListId(), request.getName(),
                                       request.getDescription());
    return commandBus.accept(command)
                     .fold(error -> badRequest().body(error.getMessage()),
                           card -> created(UriComponentsBuilder.fromPath("cards/{id}")
                                                                   .build(card.getCardId().getId())).build());
  }

}
