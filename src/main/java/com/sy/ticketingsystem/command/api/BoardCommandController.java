package com.sy.ticketingsystem.command.api;

import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

import com.sy.ticketingsystem.command.application.board.ArchiveBoardCommand;
import com.sy.ticketingsystem.command.application.board.ArchiveBoardCommandHandler;
import com.sy.ticketingsystem.command.application.board.CreateBoardCommand;
import com.sy.ticketingsystem.command.application.board.CreateBoardCommandHandler;
import com.sy.ticketingsystem.command.application.board.UpdateBoardNameCommand;
import com.sy.ticketingsystem.command.application.board.UpdateBoardNameCommandHandler;
import com.sy.ticketingsystem.core.application.CommandBus;
import java.net.URI;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@AllArgsConstructor
public class BoardCommandController {

  public static final String BOARD_COMMAND_ROOT_URL = "/boards/command/";

  public static final String CREATE_BOARD_COMMAND_URL = BOARD_COMMAND_ROOT_URL + "";

  public static final String UPDATE_BOARD_NAME_COMMAND_URL = BOARD_COMMAND_ROOT_URL + "{id}/name";

  public static final String ARCHIVE_BOARD_COMMAND_URL = BOARD_COMMAND_ROOT_URL + "{id}/archival";

  private final CommandBus commandBus;

  @PostMapping(CREATE_BOARD_COMMAND_URL)
  public ResponseEntity<?> createBoard(@RequestBody String name) {

    var command = CreateBoardCommand.newInstance(UUID.randomUUID(), name);
    UriComponentsBuilder.fromPath("boards/{id}").build(command.getId());
    return commandBus.accept(command)
                     .fold(error -> badRequest().body(error.getMessage()),
                           unit -> created(UriComponentsBuilder.fromPath("boards/{id}")
                                                               .build(command.getId())).build()
                     );
  }

  @PutMapping(UPDATE_BOARD_NAME_COMMAND_URL)
  public ResponseEntity<?> updateBoardName(@PathVariable("id") UUID id,
                                           @RequestBody String newName) {

    return commandBus.accept(new UpdateBoardNameCommand(id, newName))
                     .fold(error -> badRequest().body(error.getMessage()),
                           unit -> noContent().build()
                     );
  }

  @PutMapping(ARCHIVE_BOARD_COMMAND_URL)
  public ResponseEntity<?> archiveBoard(@PathVariable("id") UUID id) {

    return commandBus.accept(new ArchiveBoardCommand(id))
                     .fold(error -> badRequest().body(error.getMessage()),
                           unit -> noContent().build()
                     );
  }
}
