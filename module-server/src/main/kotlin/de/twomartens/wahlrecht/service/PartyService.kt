package de.twomartens.wahlrecht.service

import de.twomartens.wahlrecht.model.db.PartyInElection
import de.twomartens.wahlrecht.repository.PartyRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2

@Service
class PartyService(
    private val partyRepository: PartyRepository
) {

  fun getPartiesByElectionName(electionName: String): Flux<PartyInElection> {
    return partyRepository.findByElectionName(electionName)
        .switchIfEmpty(Flux.error(
            ResponseStatusException(HttpStatus.NOT_FOUND, "no party found for $electionName")
        ))
  }

  fun getPartyByElectionNameAndAbbreviation(electionName: String,
                                            abbreviation: String): Mono<PartyInElection> {
    return partyRepository.findByElectionNameAndAbbreviation(electionName, abbreviation)
  }

  fun storeParty(new: PartyInElection): Mono<Tuple2<Boolean, PartyInElection>> {
    return partyRepository.findAndModify(new)
  }
}
