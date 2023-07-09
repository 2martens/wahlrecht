package de.twomartens.wahlrecht.service;

import de.twomartens.wahlrecht.model.db.Candidate;
import de.twomartens.wahlrecht.repository.CandidateRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CandidateService {

  private final CandidateRepository repository;

  public Candidate storeCandidate(@NonNull Candidate candidate) {
    Optional<Candidate> foundOptional = repository.findByName(candidate.getName());

    if (foundOptional.isPresent()) {
      Candidate found = foundOptional.get();
      found.setProfession(candidate.getProfession());
      return repository.save(found);
    } else {
      return repository.save(candidate);
    }
  }
}
