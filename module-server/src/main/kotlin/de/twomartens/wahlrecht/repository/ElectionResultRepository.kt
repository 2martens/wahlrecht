package de.twomartens.wahlrecht.repository;

import de.twomartens.wahlrecht.model.db.ElectionResult;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ElectionResultRepository extends MongoRepository<ElectionResult, String>  {
}
