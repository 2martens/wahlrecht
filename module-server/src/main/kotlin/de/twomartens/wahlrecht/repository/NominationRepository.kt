package de.twomartens.wahlrecht.repository;

import de.twomartens.wahlrecht.model.db.Nomination;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface NominationRepository extends MongoRepository<Nomination, String> {

  Optional<Nomination> findByNameAndElectionNameAndPartyAbbreviation(String name,
                                                                     String electionName, String partyAbbreviation);
}
