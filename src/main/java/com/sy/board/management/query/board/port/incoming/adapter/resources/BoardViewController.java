package com.sy.board.management.query.board.port.incoming.adapter.resources;

import com.sy.board.management.query.board.port.outgoing.adapter.BoardViewRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = {"Query"})
@AllArgsConstructor
public class BoardViewController {

  public static final String BOARDS_VIEW_QUERY_ROOT_URL = "/query/boards/";

  public static final String READ_BOARD_VIEW_BY_ID_QUERY_URL = BOARDS_VIEW_QUERY_ROOT_URL + "{id}";

  private final BoardViewRepository boardViewRepository;

  @GetMapping(READ_BOARD_VIEW_BY_ID_QUERY_URL)
  @ApiOperation(value = "Read a board view by id ")
  public ResponseEntity<?> readBoardViewById(
      @PathVariable
      @ApiParam(
      value = "a board view in uuid format",
      required = true,
      example = "6e534ac1-bf9f-4af9-b4c9-d08cb4b3ece7") UUID id) {

    var exist =  boardViewRepository.findById(id.toString());
    return exist.isPresent() ?
        ResponseEntity.ok().body(exist.get()) :
        ResponseEntity.notFound().build();
  }
}
