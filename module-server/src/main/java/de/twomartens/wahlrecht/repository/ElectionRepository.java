package de.twomartens.wahlrecht.repository;

import de.twomartens.wahlrecht.model.db.Election;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ElectionRepository extends MongoRepository<Election, String> {

  Optional<Election> findByName(String name);
}
