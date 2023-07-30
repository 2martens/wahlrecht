package de.twomartens.wahlrecht.mapper.v1

import de.twomartens.wahlrecht.model.db.ElectionResult
import de.twomartens.wahlrecht.model.db.VotingResult
import org.mapstruct.*
import org.mapstruct.factory.Mappers

@Mapper(
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface ElectionResultMapper {
    companion object {
        val VOTING_RESULT_MAPPER: VotingResultMapper = Mappers.getMapper(VotingResultMapper::class.java)
    }

    fun mapToExternal(result: ElectionResult): de.twomartens.wahlrecht.model.dto.v1.ElectionResult

    fun mapToExternal(results: Collection<VotingResult>): Collection<de.twomartens.wahlrecht.model.dto.v1.VotingResult>

    fun mapToExternal(results: Map<Int, Collection<VotingResult>>):
            Map<Int, Collection<de.twomartens.wahlrecht.model.dto.v1.VotingResult>>

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    fun mapToDB(result: de.twomartens.wahlrecht.model.dto.v1.ElectionResult): ElectionResult

    fun mapToDB(results: Collection<de.twomartens.wahlrecht.model.dto.v1.VotingResult>): Collection<VotingResult>

    fun mapToDB(results: Map<Int, Collection<de.twomartens.wahlrecht.model.dto.v1.VotingResult>>):
            Map<Int, Collection<VotingResult>>

    fun mapToInternal(result: de.twomartens.wahlrecht.model.dto.v1.ElectionResult):
            de.twomartens.wahlrecht.model.internal.ElectionResult

    fun mapToInternal(result: de.twomartens.wahlrecht.model.dto.v1.VotingResult):
            de.twomartens.wahlrecht.model.internal.VotingResult {
        return VOTING_RESULT_MAPPER.mapToInternal(result)
    }

    fun mapToInternal(results: Collection<de.twomartens.wahlrecht.model.dto.v1.VotingResult>):
            Collection<de.twomartens.wahlrecht.model.internal.VotingResult>

    fun mapToInternal(results: Map<Int, Collection<de.twomartens.wahlrecht.model.dto.v1.VotingResult>>):
            Map<Int, Collection<de.twomartens.wahlrecht.model.internal.VotingResult>>
}