package de.twomartens.wahlrecht.mapper.v1;

import de.twomartens.wahlrecht.model.db.Nomination;
import de.twomartens.wahlrecht.model.internal.Candidate;
import java.util.Collection;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface NominationMapper {

  de.twomartens.wahlrecht.model.dto.v1.Nomination mapToExternal(Nomination nomination);

  de.twomartens.wahlrecht.model.internal.Nomination mapToInternal(
      de.twomartens.wahlrecht.model.dto.v1.Nomination nomination);

  de.twomartens.wahlrecht.model.internal.Nomination mapToInternal(Nomination nomination);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "created", ignore = true)
  @Mapping(target = "lastModified", ignore = true)
  Nomination mapToDB(de.twomartens.wahlrecht.model.dto.v1.Nomination nomination);

  Collection<Candidate> map(Collection<de.twomartens.wahlrecht.model.db.Candidate> candidates);
}
