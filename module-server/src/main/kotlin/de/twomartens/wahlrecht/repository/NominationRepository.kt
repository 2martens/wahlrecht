package de.twomartens.wahlrecht.repository

import de.twomartens.wahlrecht.model.db.Nomination
import org.springframework.data.mongodb.repository.MongoRepository

interface NominationRepository : MongoRepository<Nomination, String>
