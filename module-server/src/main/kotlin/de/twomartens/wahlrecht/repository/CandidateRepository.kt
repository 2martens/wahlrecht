package de.twomartens.wahlrecht.repository

import de.twomartens.wahlrecht.model.db.Candidate
import org.springframework.data.mongodb.repository.MongoRepository

interface CandidateRepository : MongoRepository<Candidate, String>