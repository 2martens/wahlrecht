package de.twomartens.wahlrecht.mapper.v1;

import de.twomartens.wahlrecht.model.db.PartyInElection;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PartyInElectionMapper {

  de.twomartens.wahlrecht.model.dto.v1.PartyInElection mapToExternal(PartyInElection election);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "lastModified", ignore = true)
  PartyInElection mapToDB(de.twomartens.wahlrecht.model.dto.v1.PartyInElection election);
}
