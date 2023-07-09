package de.twomartens.wahlrecht.model.dto.v1;

import java.util.List;

public record Nomination(String name, String partyAbbreviation, String electionName,
                         boolean supportVotesOnNomination,
                         List<Candidate> candidates) {

}
