package de.twomartens.wahlrecht.mapper.v1

import de.twomartens.wahlrecht.model.internal.Elected
import org.mapstruct.CollectionMappingStrategy
import org.mapstruct.Mapper
import org.mapstruct.NullValueCheckStrategy
import org.mapstruct.ReportingPolicy

@Mapper(
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface ElectedMapper {
    fun mapToExternal(elected: Elected): de.twomartens.wahlrecht.model.dto.v1.Elected

    fun mapToInternal(elected: de.twomartens.wahlrecht.model.dto.v1.Elected): Elected
}