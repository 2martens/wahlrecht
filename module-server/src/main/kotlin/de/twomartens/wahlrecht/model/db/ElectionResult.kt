package de.twomartens.wahlrecht.model.db

import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
data class ElectionResult(
    var electionName: String,
    var overallResults: Collection<VotingResult>,
    var constituencyResults: Map<Int, Collection<VotingResult>>
) {
    @Id
    var id: ObjectId = ObjectId()

    @CreatedDate
    lateinit var created: Instant

    @LastModifiedDate
    lateinit var lastModified: Instant
}
