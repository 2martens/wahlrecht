package de.twomartens.wahlrecht.service;

import de.twomartens.wahlrecht.model.db.Nomination;
import de.twomartens.wahlrecht.model.db.PartyInElection;
import de.twomartens.wahlrecht.model.internal.PartyId;
import de.twomartens.wahlrecht.repository.PartyRepository;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class PartyService {

  private final PartyRepository partyRepository;
  private final NominationService nominationService;
  private Map<PartyId, PartyInElection> parties = null;

  public Collection<PartyInElection> getPartiesByElectionName(String electionName) {
    Collection<PartyInElection> parties = partyRepository.findByElectionName(electionName);
    if (parties.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no party found for " + electionName);
    }

    return parties;
  }

  public boolean storeParty(PartyInElection partyInElection) {
    if (parties == null) {
      fetchParties();
    }

    boolean createdNew = true;
    PartyId partyId = new PartyId(partyInElection.getElectionName(),
        partyInElection.getAbbreviation());
    PartyInElection existing = parties.get(partyId);
    boolean needsUpdate = !partyInElection.equals(existing);
    if (!needsUpdate) {
      return false;
    }

    Map<Integer, Nomination> constituencyNominations = new HashMap<>();
    partyInElection.getConstituencyNominations()
        .forEach((key, value) -> constituencyNominations.put(key,
            nominationService.storeNomination(value)));
    if (existing != null) {
      existing.setName(partyInElection.getName());
      if (constituencyNominations.isEmpty()) {
        constituencyNominations.putAll(existing.getConstituencyNominations());
      }
      partyInElection = existing;
      createdNew = false;
    }
    partyInElection.setConstituencyNominations(constituencyNominations);
    partyInElection.setOverallNomination(
        nominationService.storeNomination(partyInElection.getOverallNomination()));
    partyInElection = partyRepository.save(partyInElection);
    parties.put(partyId, partyInElection);

    return createdNew;
  }

  private void fetchParties() {
    List<PartyInElection> allParties = partyRepository.findAll();
    parties = allParties.stream()
        .collect(Collectors.toMap(
            p -> new PartyId(p.getElectionName(), p.getAbbreviation()),
            Function.identity()));
  }
}
