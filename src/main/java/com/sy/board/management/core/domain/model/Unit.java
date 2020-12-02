package com.sy.board.management.core.domain.model;

public class Unit {

  private static final Unit INSTANCE = new Unit();

  private Unit() {
  }

  public static Unit unit() {
    return INSTANCE;
  }

  @Override
  public String toString() {
    return "<Unit>";
  }

}
