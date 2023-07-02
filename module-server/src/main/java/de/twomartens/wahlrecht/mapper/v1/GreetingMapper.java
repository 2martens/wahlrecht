package de.twomartens.wahlrecht.mapper.v1;

import de.twomartens.wahlrecht.model.db.Greeting;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GreetingMapper {

  de.twomartens.wahlrecht.model.dto.v1.Greeting map(Greeting greeting);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "lastModified", ignore = true)
  Greeting map(de.twomartens.wahlrecht.model.dto.v1.Greeting greeting);
}
