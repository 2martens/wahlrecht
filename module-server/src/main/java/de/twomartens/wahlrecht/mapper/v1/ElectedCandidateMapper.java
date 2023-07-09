package de.twomartens.wahlrecht.mapper.v1;

import de.twomartens.wahlrecht.model.internal.ElectedCandidate;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ElectedCandidateMapper {

  de.twomartens.wahlrecht.model.dto.v1.ElectedCandidate mapToExternal(ElectedCandidate candidate);

  ElectedCandidate mapToInternal(de.twomartens.wahlrecht.model.dto.v1.ElectedCandidate candidate);
}
