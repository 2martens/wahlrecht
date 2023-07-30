package de.twomartens.wahlrecht.service

import de.twomartens.wahlrecht.model.db.ElectionResult
import de.twomartens.wahlrecht.repository.ElectionResultRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class ElectionResultService(private val repository: ElectionResultRepository) {
    private val electionResults: MutableMap<String, ElectionResult> by lazy {
        fetchResults()
    }
    fun getElectionResult(electionName: String): ElectionResult {
        return electionResults[electionName]
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "no election result found for $electionName"
            )
    }

    fun storeResult(electionResult: ElectionResult): Boolean {
        var result = electionResult

        val id = result.electionName
        val existing = electionResults[id]
        val needsUpdate = result != existing
        if (!needsUpdate) {
            return false
        }
        var createdNew = true
        if (existing != null) {
            existing.overallResults = result.overallResults
            existing.constituencyResults = result.constituencyResults
            result = existing
            createdNew = false
        }
        val stored = repository.save(result)
        electionResults[id] = stored
        return createdNew
    }

    private fun fetchResults(): MutableMap<String, ElectionResult> {
        return repository.findAll().asSequence()
            .map { Pair(it.electionName, it) }
            .toMap()
            .toMutableMap()
    }
}
