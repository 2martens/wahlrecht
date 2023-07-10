package de.twomartens.wahlrecht.model.dto.v1;

import java.util.LinkedList;
import java.util.Map;
import lombok.Builder;

@Builder
public record SeatResult(Map<VotingResult, Integer> seatsPerResult,
                         LinkedList<Double> usedElectionNumbers) {

}