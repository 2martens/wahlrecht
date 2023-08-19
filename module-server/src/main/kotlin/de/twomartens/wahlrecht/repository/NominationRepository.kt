package de.twomartens.wahlrecht.repository

import de.twomartens.wahlrecht.model.db.Nomination
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

interface NominationRepository : ReactiveMongoRepository<Nomination, String> {
  fun findByElectionNameAndPartyAbbreviationAndName(electionName: String,
                                                             abbreviation: String,
                                                             name: String): Mono<Nomination>

  fun findAndModify(new: Nomination): Mono<Nomination> {
    val existing = findByElectionNameAndPartyAbbreviationAndName(
        new.electionName,
        new.partyAbbreviation,
        new.name
    )
    return existing.hasElement().flatMap { hasElement ->
      if (hasElement) {
        existing.publishOn(Schedulers.boundedElastic()).publishOn(Schedulers.boundedElastic()).flatMap { old ->
          old.supportVotesOnNomination = new.supportVotesOnNomination
          old.candidates = new.candidates
          save(old)
        }
      } else {
        save(new)
      }
    }
  }
}
