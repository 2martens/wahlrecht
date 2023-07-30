package de.twomartens.wahlrecht.mapper.v1

import de.twomartens.wahlrecht.model.db.Election
import org.mapstruct.*

@Mapper(
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface ElectionMapper {
    fun mapToExternal(election: Election): de.twomartens.wahlrecht.model.dto.v1.Election

    fun mapToInternal(
        election: de.twomartens.wahlrecht.model.dto.v1.Election
    ): de.twomartens.wahlrecht.model.internal.Election

    fun mapToInternal(election: Election): de.twomartens.wahlrecht.model.internal.Election

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    fun mapToDB(election: de.twomartens.wahlrecht.model.dto.v1.Election): Election
}