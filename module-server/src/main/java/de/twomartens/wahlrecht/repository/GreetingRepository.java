package de.twomartens.wahlrecht.repository;

import de.twomartens.wahlrecht.model.db.Greeting;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GreetingRepository extends MongoRepository<Greeting, String> {

  Optional<Greeting> findByMessageIgnoreCase(String message);

}
