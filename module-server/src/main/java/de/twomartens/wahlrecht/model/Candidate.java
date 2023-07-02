package de.twomartens.wahlrecht.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@Setter
@ToString
public class Candidate {
  private final String name;
  private final String profession;
  private Elected elected = Elected.NOT_ELECTED;
}
