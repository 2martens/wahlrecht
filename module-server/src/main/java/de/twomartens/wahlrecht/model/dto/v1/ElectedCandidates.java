package de.twomartens.wahlrecht.model.dto.v1;

import java.util.LinkedList;
import java.util.Map;

public record ElectedCandidates(ElectedResult overallResult,
                                Map<Integer, ElectedResult> constituencyResults,
                                LinkedList<Double> electionNumbersForSeatAllocation) {

}
