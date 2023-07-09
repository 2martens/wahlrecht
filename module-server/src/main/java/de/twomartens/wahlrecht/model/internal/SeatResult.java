package de.twomartens.wahlrecht.model.internal;

import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.Builder;

@Builder
public record SeatResult(Map<VotingResult, Integer> seatsPerResult,
                         LinkedList<Double> usedElectionNumbers) {

  public Map<Nomination, Integer> seatsPerNomination() {
    return seatsPerResult.entrySet().stream()
        .collect(Collectors.toMap(entry -> entry.getKey().getNomination(), Entry::getValue));
  }
}
