package com.sy.ticketingsystem.core;

public class Error {

  private String message;

  public Error(String message) {
    this.message = message;
  }

  public static Error of(Throwable throwable) {
    return  new Error(throwable.getMessage());
  }

  public static Error of(String message) {
    return new Error(message);
  }
}
