package com.sy.ticketingsystem.command.board.port.incoming.adpater.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Value;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Represents a board update name command")
@Value
@AllArgsConstructor
public class UpdateBoadNameCommandRequest {

  @ApiModelProperty(
      value = "A string representing the board name",
      example = "Project Alpha",
      required = true)
  private String newName;

  public static UpdateBoadNameCommandRequest newInstance(String newName) {

    return new UpdateBoadNameCommandRequest(newName);
  }

}
