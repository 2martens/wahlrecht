package de.twomartens.wahlrecht.repository;

import de.twomartens.wahlrecht.model.db.Constituency;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConstituencyRepository extends MongoRepository<Constituency, String> {

  Optional<Constituency> findByElectionNameAndNumber(String electionName, int number);

  Collection<Constituency> findByElectionName(String electionName);
}
