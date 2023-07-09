package de.twomartens.wahlrecht.mapper.v1;

import de.twomartens.wahlrecht.model.dto.v1.ElectedCandidate;
import de.twomartens.wahlrecht.model.dto.v1.VotingResult;
import de.twomartens.wahlrecht.model.internal.ElectedResult;
import java.util.Collection;
import java.util.Map;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ElectedResultMapper {

  de.twomartens.wahlrecht.model.dto.v1.ElectedResult mapToExternal(ElectedResult result);

  Map<VotingResult, Collection<ElectedCandidate>> mapToExternal(
      Map<de.twomartens.wahlrecht.model.internal.VotingResult,
          Collection<de.twomartens.wahlrecht.model.internal.ElectedCandidate>> value);

  Collection<ElectedCandidate> mapToExternal(
      Collection<de.twomartens.wahlrecht.model.internal.ElectedCandidate> value);

  ElectedResult mapToInternal(de.twomartens.wahlrecht.model.dto.v1.ElectedResult result);

  Map<de.twomartens.wahlrecht.model.internal.VotingResult,
      Collection<de.twomartens.wahlrecht.model.internal.ElectedCandidate>> mapToInternal(
      Map<VotingResult, Collection<ElectedCandidate>> value);

  Collection<de.twomartens.wahlrecht.model.internal.ElectedCandidate> mapToInternal(
      Collection<ElectedCandidate> value);
}
