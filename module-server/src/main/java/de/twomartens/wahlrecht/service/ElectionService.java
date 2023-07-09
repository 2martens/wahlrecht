package de.twomartens.wahlrecht.service;

import de.twomartens.wahlrecht.model.db.Constituency;
import de.twomartens.wahlrecht.model.db.Election;
import de.twomartens.wahlrecht.repository.ElectionRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ElectionService {

  private final ElectionRepository electionRepository;
  private final ConstituencyService constituencyService;

  public Collection<Election> getElections() {
    return electionRepository.findAll();
  }

  public boolean storeElection(@NonNull Election election) {
    boolean createdNew = true;
    String electionName = election.getName();
    Optional<Election> existingElectionOptional = electionRepository.findByName(electionName);
    Collection<Constituency> constituencies = new ArrayList<>();
    election.getConstituencies()
        .forEach(constituency -> constituencies.add(
            constituencyService.storeConstituency(constituency)));

    if (existingElectionOptional.isPresent()) {
      Election existing = existingElectionOptional.get();
      existing.setDay(election.getDay());
      existing.setTotalNumberOfSeats(election.getTotalNumberOfSeats());
      existing.setVotingThreshold(election.getVotingThreshold());
      election = existing;
      createdNew = false;
    }

    election.setConstituencies(constituencies);
    electionRepository.save(election);

    return createdNew;
  }
}
