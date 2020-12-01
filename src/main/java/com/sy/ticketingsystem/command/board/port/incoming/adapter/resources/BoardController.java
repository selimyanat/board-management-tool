package com.sy.ticketingsystem.command.board.port.incoming.adapter.resources;

import static java.util.UUID.randomUUID;
import static org.springframework.http.ResponseEntity.badRequest;
import static org.springframework.http.ResponseEntity.created;
import static org.springframework.http.ResponseEntity.noContent;
import static org.springframework.http.ResponseEntity.ok;

import com.sy.ticketingsystem.command.board.usecase.ArchiveBoardCommand;
import com.sy.ticketingsystem.command.board.usecase.CreateBoardCommand;
import com.sy.ticketingsystem.command.board.usecase.UpdateBoardNameCommand;
import com.sy.ticketingsystem.core.command.CommandBus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@Api(tags = {"Commands"})
@AllArgsConstructor
public class BoardController {

  public static final String BOARDS_COMMAND_ROOT_URL = "/commands/boards/";

  public static final String CREATE_BOARD_COMMAND_URL = BOARDS_COMMAND_ROOT_URL + "";

  public static final String UPDATE_BOARD_NAME_COMMAND_URL = BOARDS_COMMAND_ROOT_URL + "{id}/name";

  public static final String ARCHIVE_BOARD_COMMAND_URL = BOARDS_COMMAND_ROOT_URL + "{id}/archival";

  private final CommandBus commandBus;

  @PostMapping(CREATE_BOARD_COMMAND_URL)
  @ApiOperation(value = "Create a board")
  public ResponseEntity<?> createBoard(@RequestBody CreateBoardCommandRequest request) {

    var command = CreateBoardCommand.of(randomUUID(), request.getName());
    return commandBus.accept(command)
                     .fold(error -> badRequest().body(error.getMessage()),
                           board -> created(UriComponentsBuilder.fromPath("boards/{id}")
                                                               .build(board.getBoardId().getId())).build()
                     );
  }

  @PutMapping(UPDATE_BOARD_NAME_COMMAND_URL)
  @ApiOperation(value = "Update a board name")
  public ResponseEntity<?> updateBoardName(@ApiParam("The board id") @PathVariable("id") UUID id,
                                           @RequestBody UpdateBoadNameCommandRequest request) {

    return commandBus.accept(UpdateBoardNameCommand.of(id, request.getNewName()))
                     .fold(error -> badRequest().body(error.getMessage()),
                           board -> noContent().build()
                     );
  }

  @PutMapping(ARCHIVE_BOARD_COMMAND_URL)
  @ApiOperation(value = "Archive a board")
  public ResponseEntity<?> archiveBoard(@ApiParam("The board id") @PathVariable("id") UUID id) {

    return commandBus.accept(ArchiveBoardCommand.of(id))
                     .fold(error -> badRequest().body(error.getMessage()),
                           board -> noContent().build()
                     );
  }
}
