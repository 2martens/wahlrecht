package de.twomartens.wahlrecht.repository

import de.twomartens.wahlrecht.model.db.PartyInElection
import org.springframework.data.mongodb.repository.ReactiveMongoRepository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2

interface PartyRepository : ReactiveMongoRepository<PartyInElection, String> {

  fun findByElectionName(name: String): Flux<PartyInElection>

  fun findByElectionNameAndAbbreviation(electionName: String, abbreviation: String): Mono<PartyInElection>

  fun findAndModify(party: PartyInElection): Mono<Tuple2<Boolean, PartyInElection>> {
    val existing = findByElectionNameAndAbbreviation(party.electionName, party.abbreviation)
    return existing.hasElement().flatMap { hasElement ->
      if (hasElement) {
        existing.flatMap { old ->
          old.name = party.name
          old.overallNomination = party.overallNomination
          old.constituencyNominations = party.constituencyNominations
          Mono.just(false).zipWith(save(old))
        }
      } else {
        Mono.just(true).zipWith(save(party))
      }
    }
  }
}
