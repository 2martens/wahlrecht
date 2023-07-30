package de.twomartens.wahlrecht.service

import de.twomartens.wahlrecht.mapper.v1.ElectionMapper
import de.twomartens.wahlrecht.model.db.Constituency
import de.twomartens.wahlrecht.model.db.Election
import de.twomartens.wahlrecht.repository.ElectionRepository
import org.mapstruct.factory.Mappers
import org.springframework.stereotype.Service

@Service
class ElectionService(
    private val electionRepository: ElectionRepository,
    private val constituencyService: ConstituencyService
) {
    private val electionMapper = Mappers.getMapper(ElectionMapper::class.java)
    private val _elections: MutableMap<String, Election> by lazy {
        fetchElections()
    }

    val elections: Map<String, Election> = _elections

    private fun getElection(electionName: String): Election? {
        return elections[electionName]
    }

    fun getElectionInternal(electionName: String): de.twomartens.wahlrecht.model.internal.Election {
        return electionMapper.mapToInternal(getElection(electionName)!!)
    }

    fun storeElection(election: Election): Boolean {
        var result = election
        var createdNew = true
        val electionName = election.name
        val existing = elections[electionName]
        if (election == existing) {
            return false
        }
        val constituencies: MutableCollection<Constituency> = mutableListOf()
        result.constituencies
            .forEach { constituencies.add(constituencyService.storeConstituency(it)) }
        if (existing != null) {
            existing.day = election.day
            existing.totalNumberOfSeats = election.totalNumberOfSeats
            existing.votingThreshold = election.votingThreshold
            result = existing
            createdNew = false
        }
        result.constituencies = constituencies
        val stored = electionRepository.save(result)
        _elections[electionName] = stored
        return createdNew
    }

    private fun fetchElections(): MutableMap<String, Election> {
        return electionRepository.findAll().asSequence()
            .map { Pair(it.name, it) }
            .toMap()
            .toMutableMap()
    }
}
