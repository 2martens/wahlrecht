package de.twomartens.wahlrecht.model.dto.v1;

import java.util.Map;

public record VotingResult(String electionName, String partyAbbreviation, String nominationName,
                           int votesOnNomination, int votesThroughHealing,
                           Map<Integer, Integer> votesPerPosition) {

}
