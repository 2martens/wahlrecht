package de.twomartens.template.repository;

import de.twomartens.template.model.db.Greeting;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GreetingRepository extends MongoRepository<Greeting, String> {

  Optional<Greeting> findByMessageIgnoreCase(String message);

}
