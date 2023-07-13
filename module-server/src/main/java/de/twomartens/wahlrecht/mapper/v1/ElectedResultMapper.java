package de.twomartens.wahlrecht.mapper.v1;

import de.twomartens.wahlrecht.model.dto.v1.ElectedCandidate;
import de.twomartens.wahlrecht.model.internal.ElectedResult;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;
import org.springframework.lang.NonNull;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ElectedResultMapper {

  de.twomartens.wahlrecht.model.dto.v1.ElectedResult mapToExternal(ElectedResult result);

  default Map<String, Collection<ElectedCandidate>> mapToExternal(
      @NonNull Map<de.twomartens.wahlrecht.model.internal.VotingResult,
          Collection<de.twomartens.wahlrecht.model.internal.ElectedCandidate>> value) {
    return value.entrySet().stream()
        .collect(Collectors.toMap(
            entry -> entry.getKey().getNominationId().partyAbbreviation(),
            entry -> mapToExternal(entry.getValue())));
  }

  Collection<ElectedCandidate> mapToExternal(
      Collection<de.twomartens.wahlrecht.model.internal.ElectedCandidate> value);

  default Map<de.twomartens.wahlrecht.model.internal.NominationId,
      Collection<de.twomartens.wahlrecht.model.internal.ElectedCandidate>> map(
      @NonNull Map<de.twomartens.wahlrecht.model.internal.VotingResult,
          Collection<de.twomartens.wahlrecht.model.internal.ElectedCandidate>> value) {
    return value.entrySet().stream()
        .collect(Collectors.toMap(entry -> entry.getKey().getNominationId(), Entry::getValue));
  }

}
