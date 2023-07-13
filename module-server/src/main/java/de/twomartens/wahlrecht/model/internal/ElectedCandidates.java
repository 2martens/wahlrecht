package de.twomartens.wahlrecht.model.internal;

import java.util.LinkedList;
import java.util.Map;
import lombok.Builder;

@Builder
public record ElectedCandidates(ElectedResult overallResult,
                                Map<Integer, ElectedResult> constituencyResults,
                                LinkedList<Double> electionNumbersForSeatAllocation) {

}
