package de.twomartens.wahlrecht.service;

import de.twomartens.wahlrecht.model.db.Constituency;
import de.twomartens.wahlrecht.repository.ConstituencyRepository;
import java.util.Collection;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConstituencyService {

  private final ConstituencyRepository repository;

  public Collection<Constituency> getConstituencies() {
    return repository.findAll();
  }

  public Constituency storeConstituency(@NonNull Constituency constituency) {
    Optional<Constituency> foundOptional = repository
        .findByElectionNameAndNumber(constituency.getElectionName(), constituency.getNumber());

    if (foundOptional.isPresent()) {
      Constituency found = foundOptional.get();
      found.setNumberOfSeats(constituency.getNumberOfSeats());
      found.setName(constituency.getName());
      constituency = found;
    }
    return repository.save(constituency);
  }
}
