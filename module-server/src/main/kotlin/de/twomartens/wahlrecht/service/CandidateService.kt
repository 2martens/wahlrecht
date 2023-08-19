package de.twomartens.wahlrecht.service

import de.twomartens.wahlrecht.model.db.Candidate
import de.twomartens.wahlrecht.repository.CandidateRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class CandidateService(private val repository: CandidateRepository) {

  fun storeCandidate(candidate: Candidate): Mono<Candidate> {
    return repository.findAndModify(candidate)
  }

  fun storeCandidates(candidates: Collection<Candidate>): Flux<Candidate> {
    return Flux.merge(candidates.map { storeCandidate(it) })
  }
}
