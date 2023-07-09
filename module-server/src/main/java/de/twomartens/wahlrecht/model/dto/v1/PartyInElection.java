package de.twomartens.wahlrecht.model.dto.v1;

import java.util.Map;

public record PartyInElection(String electionName, String abbreviation, String name,
                              Nomination overallNomination,
                              Map<Integer, Nomination> constituencyNominations) {

}
