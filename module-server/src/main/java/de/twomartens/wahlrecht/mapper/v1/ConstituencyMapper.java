package de.twomartens.wahlrecht.mapper.v1;

import de.twomartens.wahlrecht.model.db.Constituency;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConstituencyMapper {

  de.twomartens.wahlrecht.model.dto.v1.Constituency mapToExternal(Constituency constituency);

  de.twomartens.wahlrecht.model.internal.Constituency mapToInternal(
      de.twomartens.wahlrecht.model.dto.v1.Constituency constituency);

  de.twomartens.wahlrecht.model.internal.Constituency mapToInternal(Constituency constituency);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "lastModified", ignore = true)
  Constituency mapToDB(de.twomartens.wahlrecht.model.dto.v1.Constituency constituency);
}
