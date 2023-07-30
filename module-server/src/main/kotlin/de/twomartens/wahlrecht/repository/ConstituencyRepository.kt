package de.twomartens.wahlrecht.repository

import de.twomartens.wahlrecht.model.db.Constituency
import org.springframework.data.mongodb.repository.MongoRepository

interface ConstituencyRepository : MongoRepository<Constituency, String>
