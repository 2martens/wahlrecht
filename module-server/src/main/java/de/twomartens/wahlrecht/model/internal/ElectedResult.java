package de.twomartens.wahlrecht.model.internal;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record ElectedResult(Map<VotingResult, Collection<ElectedCandidate>> electedCandidates,
                            LinkedList<Double> usedElectionNumbers) {

  public Map<Nomination, Collection<ElectedCandidate>> electedCandidatesByNomination() {
    return electedCandidates.entrySet().stream()
        .collect(Collectors.toMap(entry -> entry.getKey().getNomination(), Entry::getValue));
  }

}
