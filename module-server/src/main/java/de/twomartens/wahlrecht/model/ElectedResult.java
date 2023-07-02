package de.twomartens.wahlrecht.model;

import java.util.Collection;
import java.util.Deque;
import java.util.Map;
import lombok.Builder;

@Builder
public record ElectedResult(Map<Nomination, Collection<Candidate>> electedCandidates,
                            Deque<Double> usedElectionNumbers) {

}
