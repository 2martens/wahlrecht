package de.twomartens.wahlrecht.repository;

import de.twomartens.wahlrecht.model.db.Nomination;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NominationRepository extends MongoRepository<Nomination, String> {

  Optional<Nomination> findByNameAndElectionNameAndPartyAbbreviation(String name,
      String electionName, String partyAbbreviation);
}
