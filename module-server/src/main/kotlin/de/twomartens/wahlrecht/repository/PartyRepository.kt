package de.twomartens.wahlrecht.repository

import de.twomartens.wahlrecht.model.db.PartyInElection
import org.springframework.data.mongodb.repository.MongoRepository

interface PartyRepository : MongoRepository<PartyInElection, String> {
    fun findByElectionName(name: String): Collection<PartyInElection>

    fun findByElectionNameAndAbbreviation(electionName: String, abbreviation: String): PartyInElection?
}
