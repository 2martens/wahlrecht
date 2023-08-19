package de.twomartens.wahlrecht.service

import de.twomartens.wahlrecht.mapper.v1.ElectionMapper
import de.twomartens.wahlrecht.model.db.Election
import de.twomartens.wahlrecht.repository.ElectionRepository
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2

@Service
class ElectionService(
    private val electionRepository: ElectionRepository
) {
  private val electionMapper = Mappers.getMapper(ElectionMapper::class.java)

  fun getElections(): Flux<Election> {
    return electionRepository.findAll()
  }

  fun findElectionByName(electionName: String): Mono<Election> {
    return electionRepository.findByName(electionName)
  }

  fun getElectionInternal(electionName: String): Mono<de.twomartens.wahlrecht.model.internal.Election> {
    return findElectionByName(electionName).map {
      electionMapper.mapToInternal(it)
    }
  }

  fun storeElection(new: Election): Mono<Tuple2<Boolean, Election>> {
    return electionRepository.findAndModify(new)
  }
}
