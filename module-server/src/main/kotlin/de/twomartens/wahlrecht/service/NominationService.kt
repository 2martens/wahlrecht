package de.twomartens.wahlrecht.service

import de.twomartens.wahlrecht.mapper.v1.NominationMapper
import de.twomartens.wahlrecht.model.db.Candidate
import de.twomartens.wahlrecht.model.db.Nomination
import de.twomartens.wahlrecht.model.internal.NominationId
import de.twomartens.wahlrecht.repository.NominationRepository
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service

@Service
class NominationService(
    private val repository: NominationRepository,
    private val candidateService: CandidateService
) {
    private val mapper = Mappers.getMapper(NominationMapper::class.java)
    private val nominations: MutableMap<NominationId, Nomination> by lazy {
        fetchNominations()
    }

    fun getNominationInternal(id: NominationId): de.twomartens.wahlrecht.model.internal.Nomination {
        return mapper.mapToInternal(getNomination(id)!!)
    }

    fun storeNomination(nomination: Nomination?): Nomination? {
        if (nomination == null) return null
        val nominationId = NominationId(
            nomination.electionName,
            nomination.partyAbbreviation,
            nomination.name
        )
        val existing = nominations[nominationId]
        val needsUpdate = nomination != existing
        if (!needsUpdate) {
            return existing
        }
        var result: Nomination = nomination
        val candidates: MutableCollection<Candidate> = ArrayList()
        result.candidates
            .forEach { candidates.add(candidateService.storeCandidate(it)) }
        if (existing != null) {
            result = existing
        }
        result.candidates = candidates
        result = repository.save(nomination)
        nominations[nominationId] = result
        return result
    }

    private fun getNomination(id: NominationId): Nomination? {
        return nominations[id]
    }

    private fun fetchNominations(): MutableMap<NominationId, Nomination> {
        return repository.findAll().asSequence()
            .map {
                Pair(
                    NominationId(
                        electionName = it.electionName,
                        partyAbbreviation = it.partyAbbreviation,
                        name = it.name
                    ),
                    it
                )
            }
            .toMap()
            .toMutableMap()
    }
}