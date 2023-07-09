package de.twomartens.wahlrecht.service;

import de.twomartens.wahlrecht.model.db.Nomination;
import de.twomartens.wahlrecht.model.db.PartyInElection;
import de.twomartens.wahlrecht.repository.PartyRepository;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class PartyService {

  private final PartyRepository partyRepository;
  private final NominationService nominationService;

  public Collection<PartyInElection> getPartiesByElectionName(String electionName) {
    Collection<PartyInElection> parties = partyRepository.findByElectionName(electionName);
    if (parties.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "no party found for " + electionName);
    }

    return parties;
  }

  public PartyInElection getPartyByElectionNameAndAbbreviation(String electionName,
      String abbreviation) {
    Optional<PartyInElection> optionalParty = partyRepository.findByAbbreviationAndElectionName(
        abbreviation, electionName);
    if (optionalParty.isEmpty()) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "no party found for %s and %s".formatted(electionName, abbreviation));
    }
    return optionalParty.get();
  }

  public boolean storeParty(PartyInElection partyInElection) {
    boolean createdNew = true;
    Optional<PartyInElection> existingOptional = partyRepository
        .findByAbbreviationAndElectionName(partyInElection.getAbbreviation(),
            partyInElection.getElectionName());
    Map<Integer, Nomination> constituencyNominations = new HashMap<>();
    partyInElection.getConstituencyNominations()
        .forEach((key, value) -> constituencyNominations.put(key,
            nominationService.storeNomination(value)));
    if (existingOptional.isPresent()) {
      PartyInElection existing = existingOptional.get();
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
    partyRepository.save(partyInElection);

    return createdNew;
  }
}
