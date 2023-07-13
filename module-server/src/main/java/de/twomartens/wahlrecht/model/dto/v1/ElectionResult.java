package de.twomartens.wahlrecht.model.dto.v1;

import java.util.Collection;
import java.util.Map;

public record ElectionResult(String electionName, Collection<VotingResult> overallResults,
                             Map<Integer, Collection<VotingResult>> constituencyResults) {

}
