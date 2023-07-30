package de.twomartens.wahlrecht.service

import de.twomartens.wahlrecht.model.db.Candidate
import de.twomartens.wahlrecht.repository.CandidateRepository
import org.springframework.stereotype.Service

@Service
class CandidateService(private val repository: CandidateRepository) {
    private val candidates: MutableMap<String, Candidate> by lazy {
        fetchCandidates()
    }

    fun storeCandidate(candidate: Candidate): Candidate {
        var result = candidate
        val existing = candidates[candidate.name]
        val needsUpdate = candidate != existing
        if (!needsUpdate && existing != null) {
            return existing
        }
        if (existing != null) {
            existing.profession = candidate.profession
            result = existing
        }
        result = repository.save(result)
        candidates[candidate.name] = candidate
        return result
    }

    private fun fetchCandidates(): MutableMap<String, Candidate> {
        return repository.findAll().asSequence()
            .map { Pair(it.name, it) }
            .toMap()
            .toMutableMap()
    }
}
