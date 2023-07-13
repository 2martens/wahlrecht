package de.twomartens.wahlrecht.repository;

import de.twomartens.wahlrecht.model.db.ElectionResult;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ElectionResultRepository extends MongoRepository<ElectionResult, String>  {
  Optional<ElectionResult> findByElectionName(String name);
}
