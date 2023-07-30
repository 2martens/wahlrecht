package de.twomartens.wahlrecht.model.db

import lombok.EqualsAndHashCode
import org.bson.types.ObjectId
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document
@EqualsAndHashCode
@CompoundIndex(def = "{'name': 1, 'partyAbbreviation': 1, 'electionName': 1}")
class Nomination {
    @EqualsAndHashCode.Exclude
    @Id
    var id: ObjectId? = null

    @EqualsAndHashCode.Exclude
    @CreatedDate
    var created: Instant? = null

    @EqualsAndHashCode.Exclude
    @LastModifiedDate
    var lastModified: Instant? = null

    var name: String = ""
    var partyAbbreviation: String = ""
    var electionName: String = ""
    var candidates: Collection<Candidate> = emptyList()
    var supportVotesOnNomination = false
}
