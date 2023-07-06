package de.twomartens.wahlrecht.model;

public record ElectedCandidate(Candidate candidate, Elected elected) {
  public String name() {
    return candidate.name();
  }

  public String profession() {
    return candidate.profession();
  }
}
