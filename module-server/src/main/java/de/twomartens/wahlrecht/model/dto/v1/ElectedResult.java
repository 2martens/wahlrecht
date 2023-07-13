package de.twomartens.wahlrecht.model.dto.v1;

import de.twomartens.wahlrecht.model.internal.NominationId;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import lombok.Builder;

@Builder
public record ElectedResult(Map<NominationId, Collection<ElectedCandidate>> electedCandidates,
                            LinkedList<Double> usedElectionNumbers) {

}
