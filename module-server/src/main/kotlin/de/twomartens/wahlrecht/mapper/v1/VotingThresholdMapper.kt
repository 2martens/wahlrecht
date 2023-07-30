package de.twomartens.wahlrecht.mapper.v1

import de.twomartens.wahlrecht.model.internal.VotingThreshold
import org.mapstruct.CollectionMappingStrategy
import org.mapstruct.Mapper
import org.mapstruct.NullValueCheckStrategy
import org.mapstruct.ReportingPolicy

@Mapper(
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface VotingThresholdMapper {
    fun mapToExternal(threshold: VotingThreshold): de.twomartens.wahlrecht.model.dto.v1.VotingThreshold

    fun mapToInternal(threshold: de.twomartens.wahlrecht.model.dto.v1.VotingThreshold): VotingThreshold
}