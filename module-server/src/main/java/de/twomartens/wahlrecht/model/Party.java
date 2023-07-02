package de.twomartens.wahlrecht.model;

import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Setter
@Getter
public class Party {
  private final String name;
  private Nomination overallNomination;
  private Map<Constituency, Nomination> constituencyNominations;
  private boolean passesVotingThreshold;

  @Override
  public String toString() {
    return name;
  }
}
