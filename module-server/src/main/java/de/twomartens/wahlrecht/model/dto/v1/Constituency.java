package de.twomartens.wahlrecht.model.dto.v1;

public record Constituency(String electionName, int number, String name, int numberOfSeats) {

  @Override
  public String toString() {
    return name;
  }
}
