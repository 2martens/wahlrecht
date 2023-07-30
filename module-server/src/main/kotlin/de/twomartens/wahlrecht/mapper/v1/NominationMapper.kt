package de.twomartens.wahlrecht.mapper.v1

import de.twomartens.wahlrecht.model.db.Candidate
import de.twomartens.wahlrecht.model.db.Nomination
import de.twomartens.wahlrecht.model.internal.NominationId
import org.mapstruct.*

@Mapper(
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface NominationMapper {
    fun mapToExternal(nomination: Nomination): de.twomartens.wahlrecht.model.dto.v1.Nomination

    fun mapToInternal(
        nomination: de.twomartens.wahlrecht.model.dto.v1.Nomination
    ): de.twomartens.wahlrecht.model.internal.Nomination {
        val result = de.twomartens.wahlrecht.model.internal.Nomination(
            id = NominationId(
                electionName = nomination.electionName,
                partyAbbreviation = nomination.partyAbbreviation,
                name = nomination.name
            ),
            supportVotesOnNomination = nomination.supportVotesOnNomination
        )
        result.addCandidates(mapFromDto(nomination.candidates))
        return result
    }

    fun mapToInternal(nomination: Nomination): de.twomartens.wahlrecht.model.internal.Nomination {
        val result = de.twomartens.wahlrecht.model.internal.Nomination(
            id = NominationId(
                electionName = nomination.electionName,
                partyAbbreviation = nomination.partyAbbreviation,
                name = nomination.name
            ),
            supportVotesOnNomination = nomination.supportVotesOnNomination
        )
        result.addCandidates(mapFromDb(nomination.candidates))
        return result
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    fun mapToDB(nomination: de.twomartens.wahlrecht.model.dto.v1.Nomination): Nomination

    fun mapFromDb(candidates: Collection<Candidate>): Collection<de.twomartens.wahlrecht.model.internal.Candidate>

    fun mapFromDto(candidates: Collection<de.twomartens.wahlrecht.model.dto.v1.Candidate>): Collection<de.twomartens.wahlrecht.model.internal.Candidate>
}