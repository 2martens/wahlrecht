package de.twomartens.wahlrecht.model.internal;

import java.time.LocalDate;
import java.util.Collection;

public record Election(String name, LocalDate day, VotingThreshold votingThreshold,
                       int totalNumberOfSeats,
                       Collection<Constituency> constituencies) {

}
