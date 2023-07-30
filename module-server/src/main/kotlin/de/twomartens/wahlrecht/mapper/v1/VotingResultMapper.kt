package de.twomartens.wahlrecht.mapper.v1

import de.twomartens.wahlrecht.model.internal.NominationId
import de.twomartens.wahlrecht.model.internal.VotingResult
import org.mapstruct.CollectionMappingStrategy
import org.mapstruct.Mapper
import org.mapstruct.NullValueCheckStrategy
import org.mapstruct.ReportingPolicy
import org.springframework.lang.NonNull

@Mapper(
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface VotingResultMapper {
    fun mapToExternal(result: VotingResult): de.twomartens.wahlrecht.model.dto.v1.VotingResult {
        return de.twomartens.wahlrecht.model.dto.v1.VotingResult(
            electionName = result.nominationId.electionName,
            partyAbbreviation = result.nominationId.partyAbbreviation,
            nominationName = result.nominationId.name,
            votesOnNomination = result.votesOnNomination,
            votesThroughHealing = result.votesThroughHealing,
            votesPerPosition = result.votesPerPosition
        )
    }

    fun mapToInternal(@NonNull result: de.twomartens.wahlrecht.model.dto.v1.VotingResult): VotingResult {
        return VotingResult(
            nominationId = NominationId(
                electionName = result.electionName,
                partyAbbreviation = result.partyAbbreviation,
                name = result.nominationName
            ),
            votesOnNomination = result.votesOnNomination,
            votesThroughHealing = result.votesThroughHealing,
            votesPerPosition = result.votesPerPosition
        )
    }
}