package de.twomartens.wahlrecht.model.dto.v1;

import java.util.List;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@EqualsAndHashCode
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Nomination {

  String electionName;
  String partyAbbreviation;
  String name;
  boolean supportVotesOnNomination;
  List<Candidate> candidates;

}
