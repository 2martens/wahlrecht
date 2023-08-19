package de.twomartens.wahlrecht.repository

import de.twomartens.wahlrecht.model.db.Constituency
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono

interface ConstituencyRepository : ReactiveMongoRepository<Constituency, String> {
  fun findByElectionNameAndNumber(electionName: String, number: Int): Mono<Constituency>

  fun findAndModify(new: Constituency): Mono<Constituency> {
    val existing = findByElectionNameAndNumber(
        new.electionName,
        new.number)

    return existing.hasElement().flatMap { hasElement ->
      if (hasElement) {
        existing.flatMap { old ->
          old.name = new.name
          old.numberOfSeats = new.numberOfSeats
          save(old)
        }
      } else {
        save(new)
      }
    }
  }
}
