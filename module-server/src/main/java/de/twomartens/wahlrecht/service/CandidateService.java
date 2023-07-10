package de.twomartens.wahlrecht.service;

import de.twomartens.wahlrecht.model.db.Candidate;
import de.twomartens.wahlrecht.repository.CandidateRepository;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CandidateService {

  private final CandidateRepository repository;
  private Map<String, Candidate> candidates;

  public Candidate storeCandidate(@NonNull Candidate candidate) {
    if (candidates == null) {
      fetchCandidates();
    }
    Candidate existing = candidates.get(candidate.getName());
    boolean needsUpdate = !candidate.equals(existing);
    if (!needsUpdate) {
      return existing;
    }

    if (existing != null) {
      existing.setProfession(candidate.getProfession());
      candidate = existing;
    }

    candidate = repository.save(candidate);
    candidates.put(candidate.getName(), candidate);
    return candidate;
  }

  private void fetchCandidates() {
    candidates = repository.findAll().stream()
        .collect(Collectors.toMap(Candidate::getName, Function.identity()));
  }
}
