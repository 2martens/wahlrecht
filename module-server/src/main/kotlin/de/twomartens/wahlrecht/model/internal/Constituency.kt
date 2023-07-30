package de.twomartens.wahlrecht.model.internal;

public record Constituency(String electionName, int number, String name, int numberOfSeats) {

  @Override
  public String toString() {
    return name;
  }
}
