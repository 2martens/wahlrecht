package de.twomartens.wahlrecht.model;

public record Constituency(String name, int numberOfSeats) {

  @Override
  public String toString() {
    return name;
  }
}
