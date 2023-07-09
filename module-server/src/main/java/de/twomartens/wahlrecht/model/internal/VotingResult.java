package de.twomartens.wahlrecht.model.internal;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Getter
public class VotingResult {

  private final int votesOnNomination;
  private final int votesThroughHealing;
  private final Map<Integer, Integer> votesPerPosition;
  private final Nomination nomination;
  private Integer totalVotes = null;

  @Builder
  public VotingResult(int votesOnNomination, int votesThroughHealing,
      Map<Integer, Integer> votesPerPosition, Nomination nomination) {
    this.votesOnNomination = votesOnNomination;
    this.votesThroughHealing = votesThroughHealing;
    this.votesPerPosition = votesPerPosition;
    this.nomination = nomination;
  }

  public int getTotalVotes() {
    if (totalVotes == null) {
      totalVotes = votesOnNomination + votesThroughHealing + votesPerPosition.values().stream()
          .reduce(0, Integer::sum);
    }
    return totalVotes;
  }

  public int getTotalVotesWithoutHealing() {
    return getTotalVotes() - votesThroughHealing;
  }
}
