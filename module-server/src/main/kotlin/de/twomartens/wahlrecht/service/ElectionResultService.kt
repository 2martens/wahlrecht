package de.twomartens.wahlrecht.service

import de.twomartens.wahlrecht.model.db.ElectionResult
import de.twomartens.wahlrecht.repository.ElectionResultRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import reactor.util.function.Tuple2

@Service
class ElectionResultService(private val repository: ElectionResultRepository) {

  fun getElectionResult(electionName: String): Mono<ElectionResult> {
    return repository.findByElectionName(electionName)
        .switchIfEmpty(Mono.error(
            ResponseStatusException(HttpStatus.NOT_FOUND, "no election result found for $electionName")
        ))
  }

  fun storeResult(new: ElectionResult): Mono<Tuple2<Boolean, ElectionResult>> {
    return repository.findAndModify(new)
  }
}
