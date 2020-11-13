package com.sy.ticketingsystem.core.domain.model;

import java.io.Serializable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class AbstractId implements Serializable {

  private static final long serialVersionUID = 1L;

  private String id;

  protected AbstractId(String id) {

    this.id = id;
  }

}
