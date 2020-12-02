package com.sy.board.management.command.board.port.incoming.adapter.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Value;

@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Represents a card creation command")
@Value
@AllArgsConstructor(staticName = "of")
public class CreateCardRequest {

  @ApiModelProperty(
      value = "A UUID representing the card list id the card belongs to",
      example = "c0039566-ab2f-4346-9e11-1e2b88a398d2",
      required = true)
  private UUID cardListId;

  @ApiModelProperty(
      value = "A string representing the card name",
      example = "Task Name",
      required = true)
  private String name;

  @ApiModelProperty(
      value = "A string representing the card list name",
      example = "Task Description",
      required = true)
  private String description;
}
