package de.twomartens.wahlrecht.repository;

import de.twomartens.wahlrecht.model.db.VotingResult;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VotingResultRepository extends MongoRepository<VotingResult, String> {

}
