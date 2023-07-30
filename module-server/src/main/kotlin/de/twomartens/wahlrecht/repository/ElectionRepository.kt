package de.twomartens.wahlrecht.repository

import de.twomartens.wahlrecht.model.db.Election
import org.springframework.data.mongodb.repository.MongoRepository

interface ElectionRepository : MongoRepository<Election, String>
