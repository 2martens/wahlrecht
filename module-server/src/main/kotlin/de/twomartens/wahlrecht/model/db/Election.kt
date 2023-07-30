package de.twomartens.wahlrecht.model.db

import de.twomartens.wahlrecht.model.dto.v1.VotingThreshold
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.time.LocalDate

@Document
data class Election(
    @Indexed(unique = true) var name: String,
    var day: LocalDate,
    var votingThreshold: VotingThreshold,
    var totalNumberOfSeats: Int,
    var constituencies: Collection<Constituency>
) {
    @Id
    var id: ObjectId = ObjectId()

    @CreatedDate
    lateinit var created: Instant

    @LastModifiedDate
    lateinit var lastModified: Instant

}
