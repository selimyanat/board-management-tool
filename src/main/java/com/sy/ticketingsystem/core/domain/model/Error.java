package com.sy.ticketingsystem.core.domain.model;

import lombok.Value;

@Value(staticConstructor = "of")
public class Error {

  private String message;

  public static Error of(Throwable throwable) {
    return  new Error(throwable.getMessage());
  }

}
