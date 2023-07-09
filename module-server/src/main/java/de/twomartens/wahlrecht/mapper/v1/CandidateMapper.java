package de.twomartens.wahlrecht.mapper.v1;

import de.twomartens.wahlrecht.model.db.Candidate;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CandidateMapper {

  de.twomartens.wahlrecht.model.dto.v1.Candidate mapToExternal(Candidate candidate);

  de.twomartens.wahlrecht.model.internal.Candidate mapToInternal(
      de.twomartens.wahlrecht.model.dto.v1.Candidate candidate);

  de.twomartens.wahlrecht.model.internal.Candidate mapToInternal(Candidate candidate);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "lastModified", ignore = true)
  Candidate mapToDB(de.twomartens.wahlrecht.model.dto.v1.Candidate candidate);
}
