package de.twomartens.wahlrecht.model.dto.v1;

import java.util.Map;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

public record PartyInElection(String electionName, String abbreviation, String name,
                              Nomination overallNomination,
                              Map<Integer, Nomination> constituencyNominations) {

}
