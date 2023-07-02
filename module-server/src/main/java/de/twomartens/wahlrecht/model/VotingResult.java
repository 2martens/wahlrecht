package de.twomartens.wahlrecht.model;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class VotingResult {

  private final int votesOnNomination;
  private final Map<Integer, Integer> votesPerPosition;
  private Integer totalVotes = null;

  @Builder
  public VotingResult(int votesOnNomination, Map<Integer, Integer> votesPerPosition) {
    this.votesOnNomination = votesOnNomination;
    this.votesPerPosition = votesPerPosition;
  }

  public int getTotalVotes() {
    if (totalVotes == null) {
      totalVotes = votesOnNomination + votesPerPosition.values().stream()
          .reduce(0, Integer::sum);
    }
    return totalVotes;
  }
}
