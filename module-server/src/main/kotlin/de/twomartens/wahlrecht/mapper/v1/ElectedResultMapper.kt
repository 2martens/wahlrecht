package de.twomartens.wahlrecht.mapper.v1

import de.twomartens.wahlrecht.model.internal.ElectedCandidate
import de.twomartens.wahlrecht.model.internal.ElectedResult
import de.twomartens.wahlrecht.model.internal.NominationId
import de.twomartens.wahlrecht.model.internal.VotingResult
import org.mapstruct.CollectionMappingStrategy
import org.mapstruct.Mapper
import org.mapstruct.NullValueCheckStrategy
import org.mapstruct.ReportingPolicy

@Mapper(
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface ElectedResultMapper {
    fun mapToExternal(result: ElectedResult): de.twomartens.wahlrecht.model.dto.v1.ElectedResult

    fun mapToExternal(value: Map<VotingResult, Collection<ElectedCandidate>>):
            Map<String, Collection<de.twomartens.wahlrecht.model.dto.v1.ElectedCandidate>> {
        return value.entries.asSequence()
            .map { Pair(it.key.nominationId.partyAbbreviation, mapToExternal(it.value)) }
            .toMap()
    }

    fun mapToExternal(value: Collection<ElectedCandidate>):
            Collection<de.twomartens.wahlrecht.model.dto.v1.ElectedCandidate>

    fun map(value: Map<VotingResult, Collection<ElectedCandidate>>):
            Map<NominationId, Collection<ElectedCandidate>> {
        return value.entries.asSequence()
            .map { Pair(it.key.nominationId, it.value) }
            .toMap()
    }
}