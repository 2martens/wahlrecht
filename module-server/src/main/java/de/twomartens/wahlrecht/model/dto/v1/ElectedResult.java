package de.twomartens.wahlrecht.model.dto.v1;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record ElectedResult(Map<VotingResult, Collection<ElectedCandidate>> electedCandidates,
                            LinkedList<Double> usedElectionNumbers) {

}
