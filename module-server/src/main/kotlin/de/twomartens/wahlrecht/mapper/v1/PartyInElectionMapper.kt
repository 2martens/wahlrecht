package de.twomartens.wahlrecht.mapper.v1

import de.twomartens.wahlrecht.model.db.PartyInElection
import org.mapstruct.*

@Mapper(
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface PartyInElectionMapper {
    fun mapToExternal(election: PartyInElection): de.twomartens.wahlrecht.model.dto.v1.PartyInElection

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    fun mapToDB(election: de.twomartens.wahlrecht.model.dto.v1.PartyInElection): PartyInElection
}