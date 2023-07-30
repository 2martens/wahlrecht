package de.twomartens.wahlrecht.mapper.v1

import de.twomartens.wahlrecht.model.db.Candidate
import org.mapstruct.*

@Mapper(
    collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE
)
interface CandidateMapper {
    fun mapToExternal(candidate: Candidate): de.twomartens.wahlrecht.model.dto.v1.Candidate

    fun mapToInternal(
        candidate: de.twomartens.wahlrecht.model.dto.v1.Candidate
    ): de.twomartens.wahlrecht.model.internal.Candidate

    fun mapToInternal(candidate: Candidate): de.twomartens.wahlrecht.model.internal.Candidate

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    @Mapping(target = "lastModified", ignore = true)
    fun mapToDB(candidate: de.twomartens.wahlrecht.model.dto.v1.Candidate): Candidate
}