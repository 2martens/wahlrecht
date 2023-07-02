package de.twomartens.wahlrecht.model;

import java.util.Deque;
import java.util.Map;
import lombok.Builder;

@Builder
public record SeatResult(Map<Nomination, Long> seatsPerNomination,
                         Deque<Double> usedElectionNumbers) {

}
