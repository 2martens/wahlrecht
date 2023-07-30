package de.twomartens.wahlrecht.service;

import de.twomartens.wahlrecht.model.db.ElectionResult;
import de.twomartens.wahlrecht.repository.ElectionResultRepository;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
public class ElectionResultService {

  private final ElectionResultRepository repository;

  private Map<String, ElectionResult> electionResults;

  public ElectionResult getElectionResult(String electionName) {
    if (electionResults == null) {
      fetchResults();
    }
    ElectionResult electionResult = electionResults.get(electionName);
    if (electionResult == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND,
          "no election result found for %s".formatted(electionName));
    }
    return electionResult;
  }

  public boolean storeResult(ElectionResult result) {
    if (electionResults == null) {
      fetchResults();
    }

    String id = result.getElectionName();
    ElectionResult existing = electionResults.get(id);
    boolean needsUpdate = !result.equals(existing);

    if (!needsUpdate) {
      return false;
    }

    boolean createdNew = true;
    if (existing != null) {
      existing.setOverallResults(result.getOverallResults());
      existing.setConstituencyResults(result.getConstituencyResults());
      result = existing;
      createdNew = false;
    }

    ElectionResult stored = repository.save(result);
    electionResults.put(id, stored);

    return createdNew;
  }

  private void fetchResults() {
    electionResults = repository.findAll().stream()
        .collect(Collectors.toMap(
            ElectionResult::getElectionName,
            Function.identity()));
  }
}
