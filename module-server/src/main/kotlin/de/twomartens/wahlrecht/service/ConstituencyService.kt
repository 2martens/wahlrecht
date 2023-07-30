package de.twomartens.wahlrecht.service

import de.twomartens.wahlrecht.model.db.Constituency
import de.twomartens.wahlrecht.model.internal.ConstituencyId
import de.twomartens.wahlrecht.repository.ConstituencyRepository
import org.springframework.stereotype.Service

@Service
class ConstituencyService(private val repository: ConstituencyRepository) {
    private val constituencies: MutableMap<ConstituencyId, Constituency> by lazy {
        fetchConstituencies()
    }

    fun storeConstituency(constituency: Constituency): Constituency {
        var result = constituency
        val constituencyId = ConstituencyId(
            constituency.electionName,
            constituency.number
        )
        val existing = constituencies[constituencyId]
        if (constituency == existing) {
            return existing
        }
        if (existing != null) {
            existing.numberOfSeats = constituency.numberOfSeats
            existing.name = constituency.name
            result = existing
        }
        val stored = repository.save(result)
        constituencies[constituencyId] = stored
        return stored
    }

    private fun fetchConstituencies(): MutableMap<ConstituencyId, Constituency> {
        return repository.findAll().asSequence()
            .map { Pair(ConstituencyId(electionName = it.electionName, number = it.number), it) }
            .toMap()
            .toMutableMap()
    }
}
