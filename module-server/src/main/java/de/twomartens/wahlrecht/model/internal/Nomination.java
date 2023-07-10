package de.twomartens.wahlrecht.model.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.ToString.Include;

@RequiredArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
public class Nomination {

  private final String electionName;
  private final String partyAbbreviation;
  @Include
  private final String name;
  private final boolean supportVotesOnNomination;
  private final List<Candidate> candidates = new ArrayList<>();
  private final Map<Candidate, Integer> candidateToPosition = new HashMap<>();
  private final Map<Integer, Candidate> positionToCandidate = new HashMap<>();

  private void addCandidate(Candidate candidate) {
    candidates.add(candidate);
    candidateToPosition.put(candidate, candidates.size());
    positionToCandidate.put(candidates.size(), candidate);
  }

  public void addCandidates(Candidate... candidates) {
    Arrays.stream(candidates).forEach(this::addCandidate);
  }

  public void addCandidates(Collection<Candidate> candidates) {
    candidates.forEach(this::addCandidate);
  }

  public List<Candidate> getCandidates() {
    return new ArrayList<>(positionToCandidate.values());
  }

  public Candidate getCandidate(int position) {
    return positionToCandidate.get(position);
  }

  public int getPosition(Candidate candidate) {
    return candidateToPosition.get(candidate);
  }

  public boolean supportsVotesOnNomination() {
    return supportVotesOnNomination;
  }
}