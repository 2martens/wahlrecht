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
@CompoundIndex(def = "{'abbreviation': 1, 'electionName': 1}")
data class PartyInElection(
    var abbreviation: String,
    var electionName: String,
    var name: String,
    var overallNomination: Nomination,
    var constituencyNominations: Map<Int, Nomination?>
) {
    @Id
    var id: ObjectId = ObjectId()

    @EqualsAndHashCode.Exclude
    @CreatedDate
    lateinit var created: Instant

    @EqualsAndHashCode.Exclude
    @LastModifiedDate
    lateinit var lastModified: Instant
}
