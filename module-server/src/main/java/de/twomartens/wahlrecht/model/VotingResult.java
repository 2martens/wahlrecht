package de.twomartens.wahlrecht.model;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class VotingResult {

  private final int votesOnNomination;
  private final int votesThroughHealing;
  private final Map<Integer, Integer> votesPerPosition;
  private Integer totalVotes = null;

  @Builder
  public VotingResult(int votesOnNomination, int votesThroughHealing,
      Map<Integer, Integer> votesPerPosition) {
    this.votesOnNomination = votesOnNomination;
    this.votesThroughHealing = votesThroughHealing;
    this.votesPerPosition = votesPerPosition;
  }

  public int getTotalVotes() {
    if (totalVotes == null) {
      totalVotes = votesOnNomination + votesThroughHealing + votesPerPosition.values().stream()
          .reduce(0, Integer::sum);
    }
    return totalVotes;
  }
}
