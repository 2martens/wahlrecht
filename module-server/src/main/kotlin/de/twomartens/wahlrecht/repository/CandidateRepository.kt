package de.twomartens.wahlrecht.repository;

import de.twomartens.wahlrecht.model.db.Candidate;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CandidateRepository extends MongoRepository<Candidate, String> {

  Optional<Candidate> findByName(String name);
}
