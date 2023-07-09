package de.twomartens.wahlrecht.service;

import de.twomartens.wahlrecht.model.db.Candidate;
import de.twomartens.wahlrecht.model.db.Nomination;
import de.twomartens.wahlrecht.repository.NominationRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NominationService {

  private final NominationRepository repository;
  private final CandidateService candidateService;

  public Nomination storeNomination(Nomination nomination) {
    Optional<Nomination> existingOptional = repository
        .findByNameAndElectionNameAndPartyAbbreviation(nomination.getName(),
            nomination.getElectionName(), nomination.getPartyAbbreviation());
    Collection<Candidate> candidates = new ArrayList<>();
    nomination.getCandidates()
        .forEach(candidate -> candidates.add(candidateService.storeCandidate(candidate)));
    if (existingOptional.isPresent()) {
      nomination = existingOptional.get();
    }
    nomination.setCandidates(candidates);
    return repository.save(nomination);
  }
}
