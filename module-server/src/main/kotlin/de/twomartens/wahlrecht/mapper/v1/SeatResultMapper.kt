package de.twomartens.wahlrecht.mapper.v1

import de.twomartens.wahlrecht.model.internal.SeatResult
import org.mapstruct.CollectionMappingStrategy
import org.mapstruct.Mapper
import org.mapstruct.NullValueCheckStrategy
import org.mapstruct.ReportingPolicy

@Mapper(
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface SeatResultMapper {
    fun mapToExternal(result: SeatResult): de.twomartens.wahlrecht.model.dto.v1.SeatResult

    fun mapToInternal(result: de.twomartens.wahlrecht.model.dto.v1.SeatResult): SeatResult
}