package de.twomartens.wahlrecht.mapper.v1

import de.twomartens.wahlrecht.model.internal.ElectedCandidate
import de.twomartens.wahlrecht.model.internal.ElectedCandidates
import de.twomartens.wahlrecht.model.internal.VotingResult
import org.mapstruct.CollectionMappingStrategy
import org.mapstruct.Mapper
import org.mapstruct.NullValueCheckStrategy
import org.mapstruct.ReportingPolicy
import org.mapstruct.factory.Mappers

@Mapper(
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface ElectedCandidatesMapper {
    companion object {
        val ELECTED_RESULT_MAPPER: ElectedResultMapper = Mappers.getMapper(ElectedResultMapper::class.java)
    }


    fun mapToExternal(candidate: ElectedCandidates): de.twomartens.wahlrecht.model.dto.v1.ElectedCandidates

    fun mapToExternal(value: Map<VotingResult, Collection<ElectedCandidate>>):
            Map<String, Collection<de.twomartens.wahlrecht.model.dto.v1.ElectedCandidate>> {
        return ELECTED_RESULT_MAPPER.mapToExternal(value)
    }
}