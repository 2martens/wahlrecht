package de.twomartens.wahlrecht.mapper.v1

import de.twomartens.wahlrecht.model.db.Constituency
import org.mapstruct.*

@Mapper(
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface ConstituencyMapper {
    fun mapToExternal(constituency: Constituency): de.twomartens.wahlrecht.model.dto.v1.Constituency

    fun mapToInternal(constituency: de.twomartens.wahlrecht.model.dto.v1.Constituency): de.twomartens.wahlrecht.model.internal.Constituency

    fun mapToInternal(constituency: Constituency): de.twomartens.wahlrecht.model.internal.Constituency

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    fun mapToDB(constituency: de.twomartens.wahlrecht.model.dto.v1.Constituency): Constituency
}