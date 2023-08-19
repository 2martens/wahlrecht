package de.twomartens.wahlrecht.service

import de.twomartens.wahlrecht.mapper.v1.NominationMapper
import de.twomartens.wahlrecht.model.db.Nomination
import de.twomartens.wahlrecht.model.internal.NominationId
import de.twomartens.wahlrecht.repository.NominationRepository
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class NominationService(
    private val repository: NominationRepository
) {
  private val mapper = Mappers.getMapper(NominationMapper::class.java)

  fun getNominationInternal(id: NominationId): Mono<de.twomartens.wahlrecht.model.internal.Nomination> {
    return getNomination(id).map {
      mapper.mapToInternal(it)
    }
  }

  fun storeNomination(new: Nomination?): Mono<Nomination> {
    if (new == null) return Mono.empty()

    return repository.findAndModify(new)
  }

  fun storeNominations(new: Collection<Nomination>): Flux<Nomination> {
    return Flux.merge(new.map { storeNomination(it) })
  }

  private fun getNomination(id: NominationId): Mono<Nomination> {
    return repository.findByElectionNameAndPartyAbbreviationAndName(
        id.electionName,
        id.partyAbbreviation,
        id.name
    )
  }
}