package de.twomartens.wahlrecht.mapper.v1;

import de.twomartens.wahlrecht.model.internal.SeatResult;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SeatResultMapper {

  de.twomartens.wahlrecht.model.dto.v1.SeatResult mapToExternal(SeatResult result);

  SeatResult mapToInternal(de.twomartens.wahlrecht.model.dto.v1.SeatResult result);
}
