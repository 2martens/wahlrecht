package de.twomartens.wahlrecht.model.dto.v1;

import java.util.Map;
import lombok.Builder;
import lombok.Getter;

public record VotingResult(int votesOnNomination, int votesThroughHealing, Integer totalVotes,
                           Map<Integer, Integer> votesPerPosition, String nominationName) {

}
