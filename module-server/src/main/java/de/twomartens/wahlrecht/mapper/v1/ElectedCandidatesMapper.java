package de.twomartens.wahlrecht.mapper.v1;

import de.twomartens.wahlrecht.model.internal.ElectedCandidate;
import de.twomartens.wahlrecht.model.internal.ElectedCandidates;
import de.twomartens.wahlrecht.model.internal.VotingResult;
import java.util.Collection;
import java.util.Map;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ElectedCandidatesMapper {

  ElectedResultMapper ELECTED_RESULT_MAPPER = Mappers.getMapper(ElectedResultMapper.class);


  de.twomartens.wahlrecht.model.dto.v1.ElectedCandidates mapToExternal(ElectedCandidates candidate);

  default Map<String, Collection<de.twomartens.wahlrecht.model.dto.v1.ElectedCandidate>> mapToExternal(
      Map<VotingResult, Collection<ElectedCandidate>> value) {
    return ELECTED_RESULT_MAPPER.mapToExternal(value);
  }
}
