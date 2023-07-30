package de.twomartens.wahlrecht.repository;

import de.twomartens.wahlrecht.model.db.PartyInElection;
import java.util.Collection;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PartyRepository extends MongoRepository<PartyInElection, String> {

  Optional<PartyInElection> findByAbbreviationAndElectionName(String abbreviation,
      String electionName);

  Collection<PartyInElection> findByElectionName(String name);
}
