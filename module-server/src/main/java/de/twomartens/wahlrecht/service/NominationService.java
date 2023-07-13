package de.twomartens.wahlrecht.service;

import de.twomartens.wahlrecht.mapper.v1.NominationMapper;
import de.twomartens.wahlrecht.model.db.Candidate;
import de.twomartens.wahlrecht.model.db.Nomination;
import de.twomartens.wahlrecht.model.internal.NominationId;
import de.twomartens.wahlrecht.repository.NominationRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class NominationService {

  private final NominationMapper mapper = Mappers.getMapper(NominationMapper.class);
  private final NominationRepository repository;
  private final CandidateService candidateService;
  private Map<NominationId, Nomination> nominations;

  public Nomination getNomination(NominationId id) {
    if (nominations == null) {
      fetchNominations();
    }
    return nominations.get(id);
  }

  public de.twomartens.wahlrecht.model.internal.Nomination getNominationInternal(NominationId id) {
    return mapper.mapToInternal(getNomination(id));
  }

  public Nomination storeNomination(Nomination nomination) {
    if (nomination == null) {
      return null;
    }

    if (nominations == null) {
      fetchNominations();
    }

    NominationId nominationId = new NominationId(nomination.getElectionName(),
        nomination.getPartyAbbreviation(), nomination.getName());
    Nomination existing = nominations.get(nominationId);
    boolean needsUpdate = !nomination.equals(existing);

    if (!needsUpdate) {
      return existing;
    }

    Collection<Candidate> candidates = new ArrayList<>();
    nomination.getCandidates()
        .forEach(candidate -> candidates.add(candidateService.storeCandidate(candidate)));
    if (existing != null) {
      nomination = existing;
    }
    nomination.setCandidates(candidates);
    nomination = repository.save(nomination);
    nominations.put(nominationId, nomination);

    return nomination;
  }

  private void fetchNominations() {
    nominations = repository.findAll().stream()
        .collect(Collectors.toMap(
            n -> new NominationId(n.getElectionName(), n.getPartyAbbreviation(), n.getName()),
            Function.identity()));
  }
}
