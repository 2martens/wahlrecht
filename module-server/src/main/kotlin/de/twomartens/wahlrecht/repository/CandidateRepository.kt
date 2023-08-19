package de.twomartens.wahlrecht.repository

import de.twomartens.wahlrecht.model.db.Candidate
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CandidateRepository : ReactiveMongoRepository<Candidate, String> {
  fun findByName(name: String): Mono<Candidate>

  fun findAndModify(new: Candidate): Mono<Candidate> {
    val existing = findByName(new.name)
    return existing.hasElement().flatMap { hasElement ->
      if (hasElement) {
        existing.flatMap { old ->
          old.profession = new.profession
          save(old)
        }
      } else {
        save(new)
      }
    }
  }

  fun findAllAndModify(new: Collection<Candidate>): Flux<Candidate> {
    val existing = new.map {
      findAndModify(it)
    }
    return Flux.merge(existing)
  }
}