package de.twomartens.wahlrecht.model.dto.v1;

import java.util.HashMap;
import java.util.Map;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.ToString.Include;
import lombok.experimental.FieldDefaults;
import org.springframework.lang.NonNull;

@Getter
@Setter
@EqualsAndHashCode
@ToString(onlyExplicitlyIncluded = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PartyInElection {
    String electionName;
    @Include
    String abbreviation;
    String name;
    Nomination overallNomination;
    @NonNull
    Map<Integer, Nomination> constituencyNominations = new HashMap<>();
}
