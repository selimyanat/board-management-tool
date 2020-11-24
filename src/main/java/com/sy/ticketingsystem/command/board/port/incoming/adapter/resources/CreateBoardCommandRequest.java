package com.sy.ticketingsystem.command.board.port.incoming.adapter.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Value;


@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Represents a board creation command")
@Value
@AllArgsConstructor(staticName = "of")
public class CreateBoardCommandRequest {

  @ApiModelProperty(
      value = "A string representing the board name",
      example = "Project Alpha",
      required = true)
  private String name;

}
