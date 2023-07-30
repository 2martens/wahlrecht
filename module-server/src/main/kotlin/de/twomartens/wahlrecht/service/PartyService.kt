package de.twomartens.wahlrecht.service

import de.twomartens.wahlrecht.model.db.Nomination
import de.twomartens.wahlrecht.model.db.PartyInElection
import de.twomartens.wahlrecht.model.internal.PartyId
import de.twomartens.wahlrecht.repository.PartyRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class PartyService(
    private val partyRepository: PartyRepository,
    private val nominationService: NominationService
) {

    private val parties: MutableMap<PartyId, PartyInElection> by lazy {
        fetchParties()
    }

    fun getPartiesByElectionName(electionName: String): Collection<PartyInElection> {
        val parties = partyRepository.findByElectionName(electionName)
        if (parties.isEmpty()) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "no party found for $electionName")
        }
        return parties
    }

    fun storeParty(partyInElection: PartyInElection): Boolean {
        var result = partyInElection
        var createdNew = true
        val partyId = PartyId(
            partyInElection.electionName,
            partyInElection.abbreviation
        )
        val existing = parties[partyId]
        val needsUpdate = partyInElection != existing
        if (!needsUpdate) {
            return false
        }
        val constituencyNominations: MutableMap<Int, Nomination?> = mutableMapOf()
        result.constituencyNominations
            .forEach { constituencyNominations[it.key] = nominationService.storeNomination(it.value) }
        if (existing != null) {
            existing.name = partyInElection.name
            if (constituencyNominations.isEmpty()) {
                constituencyNominations.putAll(existing.constituencyNominations)
            }
            result = existing
            createdNew = false
        }
        partyInElection.constituencyNominations = constituencyNominations
        partyInElection.overallNomination = nominationService.storeNomination(partyInElection.overallNomination)!!
        result = partyRepository.save(result)
        parties[partyId] = result
        return createdNew
    }

    private fun fetchParties(): MutableMap<PartyId, PartyInElection> {
        val allParties = partyRepository.findAll()
        return allParties.asSequence()
            .map { Pair(PartyId(electionName = it.electionName, abbreviation = it.abbreviation), it) }
            .toMap()
            .toMutableMap()
    }
}
