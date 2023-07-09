package de.twomartens.wahlrecht.model.dto.v1;

import java.util.Map;

public record VotingResult(int votesOnNomination, int votesThroughHealing, Integer totalVotes,
                           Map<Integer, Integer> votesPerPosition, String nominationName) {

}
