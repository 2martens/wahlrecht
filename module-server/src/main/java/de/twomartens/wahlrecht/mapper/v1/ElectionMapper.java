package de.twomartens.wahlrecht.mapper.v1;

import de.twomartens.wahlrecht.model.db.Election;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ElectionMapper {

  de.twomartens.wahlrecht.model.dto.v1.Election mapToExternal(Election election);

  de.twomartens.wahlrecht.model.internal.Election mapToInternal(
      de.twomartens.wahlrecht.model.dto.v1.Election election);

  de.twomartens.wahlrecht.model.internal.Election mapToInternal(Election election);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "lastModified", ignore = true)
  Election mapToDB(de.twomartens.wahlrecht.model.dto.v1.Election election);
}
