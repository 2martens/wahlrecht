package de.twomartens.wahlrecht.service;

import de.twomartens.wahlrecht.mapper.v1.ConstituencyMapper;
import de.twomartens.wahlrecht.model.db.Constituency;
import de.twomartens.wahlrecht.model.internal.ConstituencyId;
import de.twomartens.wahlrecht.repository.ConstituencyRepository;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConstituencyService {

  private final ConstituencyRepository repository;
  private final ConstituencyMapper mapper = Mappers.getMapper(ConstituencyMapper.class);

  private Map<ConstituencyId, Constituency> constituencies;

  public Collection<Constituency> getConstituencies() {
    return repository.findAll();
  }

  public Constituency getConstituency(ConstituencyId constituencyId) {
    return constituencies.get(constituencyId);
  }

  public de.twomartens.wahlrecht.model.internal.Constituency getConstituencyInternal(
      ConstituencyId constituencyId) {
    return mapper.mapToInternal(getConstituency(constituencyId));
  }

  public Constituency storeConstituency(@NonNull Constituency constituency) {
    if (constituencies == null) {
      fetchConstituencies();
    }

    ConstituencyId constituencyId = new ConstituencyId(constituency.getElectionName(),
        constituency.getNumber());
    Constituency existing = constituencies.get(constituencyId);

    if (constituency.equals(existing)) {
      return existing;
    }

    if (existing != null) {
      existing.setNumberOfSeats(constituency.getNumberOfSeats());
      existing.setName(constituency.getName());
      constituency = existing;
    }
    Constituency stored = repository.save(constituency);
    constituencies.put(constituencyId, stored);

    return stored;
  }

  private void fetchConstituencies() {
    constituencies = repository.findAll().stream()
        .collect(Collectors.toMap(
            constituency -> new ConstituencyId(constituency.getElectionName(),
                constituency.getNumber()),
            Function.identity()));
  }
}
