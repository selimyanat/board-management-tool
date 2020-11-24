package com.sy.ticketingsystem.command.board.port.incoming.adapter.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Value;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Represents a card list creation command")
@Value
@AllArgsConstructor(staticName = "of")
public class CreateCardListRequest {

  @ApiModelProperty(
      value = "A UUID representing the board id the list belongs to",
      example = "c0039566-ab2f-4346-9e11-1e2b88a398d2",
      required = true)
  private UUID boardId;

  @ApiModelProperty(
      value = "A string representing the card list name",
      example = "Todo List",
      required = true)
  private String name;

}
