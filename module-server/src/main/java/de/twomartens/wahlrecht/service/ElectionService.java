package de.twomartens.wahlrecht.service;

import de.twomartens.wahlrecht.mapper.v1.ElectionMapper;
import de.twomartens.wahlrecht.model.db.Constituency;
import de.twomartens.wahlrecht.model.db.Election;
import de.twomartens.wahlrecht.repository.ElectionRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ElectionService {

  private final ElectionRepository electionRepository;
  private final ConstituencyService constituencyService;
  private final ElectionMapper electionMapper = Mappers.getMapper(ElectionMapper.class);

  private Map<String, Election> elections;

  public Collection<Election> getElections() {
    return electionRepository.findAll();
  }

  public Election getElection(String electionName) {
    if (elections == null) {
      fetchElections();
    }
    return elections.get(electionName);
  }

  public de.twomartens.wahlrecht.model.internal.Election getElectionInternal(String electionName) {
    return electionMapper.mapToInternal(getElection(electionName));
  }

  public boolean storeElection(@NonNull Election election) {
    if (elections == null) {
      fetchElections();
    }
    boolean createdNew = true;
    String electionName = election.getName();
    Election existing = elections.get(electionName);

    if (election.equals(existing)) {
      return false;
    }

    Collection<Constituency> constituencies = new ArrayList<>();
    election.getConstituencies()
        .forEach(constituency -> constituencies.add(
            constituencyService.storeConstituency(constituency)));

    if (existing != null) {
      existing.setDay(election.getDay());
      existing.setTotalNumberOfSeats(election.getTotalNumberOfSeats());
      existing.setVotingThreshold(election.getVotingThreshold());
      election = existing;
      createdNew = false;
    }

    election.setConstituencies(constituencies);
    Election stored = electionRepository.save(election);
    elections.put(electionName, stored);

    return createdNew;
  }

  private void fetchElections() {
    elections = electionRepository.findAll().stream()
        .collect(Collectors.toMap(Election::getName, Function.identity()));
  }
}
