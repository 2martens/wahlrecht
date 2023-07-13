package de.twomartens.wahlrecht.mapper.v1;

import de.twomartens.wahlrecht.model.internal.ElectionResult;
import de.twomartens.wahlrecht.model.internal.VotingResult;
import java.util.Collection;
import java.util.Map;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ElectionResultMapper {

  de.twomartens.wahlrecht.model.dto.v1.ElectionResult mapToExternal(de.twomartens.wahlrecht.model.db.ElectionResult result);

  Collection<de.twomartens.wahlrecht.model.dto.v1.VotingResult> mapToExternal(
      Collection<de.twomartens.wahlrecht.model.db.VotingResult> results);

  Map<Integer, Collection<de.twomartens.wahlrecht.model.dto.v1.VotingResult>> mapToExternal(
      Map<Integer, Collection<de.twomartens.wahlrecht.model.db.VotingResult>> results);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "lastModified", ignore = true)
  de.twomartens.wahlrecht.model.db.ElectionResult mapToDB(de.twomartens.wahlrecht.model.dto.v1.ElectionResult result);

  Collection<de.twomartens.wahlrecht.model.db.VotingResult> mapToDB(
      Collection<de.twomartens.wahlrecht.model.dto.v1.VotingResult> results);

  Map<Integer, Collection<de.twomartens.wahlrecht.model.db.VotingResult>> mapToDB(
      Map<Integer, Collection<de.twomartens.wahlrecht.model.dto.v1.VotingResult>> results);

  ElectionResult mapToInternal(de.twomartens.wahlrecht.model.dto.v1.ElectionResult result);

  Collection<VotingResult> mapToInternal(
      Collection<de.twomartens.wahlrecht.model.dto.v1.VotingResult> results);

  Map<Integer, Collection<VotingResult>> mapToInternal(
      Map<Integer, Collection<de.twomartens.wahlrecht.model.dto.v1.VotingResult>> results);
}
