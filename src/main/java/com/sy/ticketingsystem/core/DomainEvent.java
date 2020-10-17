package com.sy.ticketingsystem.core;

public abstract class DomainEvent <T> {

  public abstract T rehydrate(T t);
}
