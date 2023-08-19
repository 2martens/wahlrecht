package de.twomartens.wahlrecht.repository

import de.twomartens.wahlrecht.model.db.Election
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2

interface ElectionRepository : ReactiveMongoRepository<Election, String> {
  fun findByName(name: String): Mono<Election>

  fun findAndModify(new: Election): Mono<Tuple2<Boolean, Election>> {
    val electionName = new.name
    val existing = findByName(electionName)
    return existing.hasElement().flatMap { hasElement ->
      if (hasElement) {
        existing.flatMap { old ->
          old.constituencies = new.constituencies
          old.totalNumberOfSeats = new.totalNumberOfSeats
          old.votingThreshold = new.votingThreshold
          Mono.just(false).zipWith(save(old))
        }
      } else {
        Mono.just(true).zipWith(save(new))
      }
    }
  }
}
