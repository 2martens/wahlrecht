package de.twomartens.wahlrecht.repository

import de.twomartens.wahlrecht.model.db.ElectionResult
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2

interface ElectionResultRepository : ReactiveMongoRepository<ElectionResult, String> {
  fun findByElectionName(electionName: String): Mono<ElectionResult>

  fun findAndModify(new: ElectionResult): Mono<Tuple2<Boolean, ElectionResult>> {
    val existing = findByElectionName(new.electionName)
    return existing.hasElement().flatMap { hasElement ->
      if (hasElement) {
        existing.flatMap { old ->
          old.overallResults = new.overallResults
          old.constituencyResults = new.constituencyResults
          Mono.just(false).zipWith(save(old))
        }
      } else {
        Mono.just(true).zipWith(save(new))
      }
    }
  }
}
