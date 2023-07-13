package de.twomartens.wahlrecht.mapper.v1;

import de.twomartens.wahlrecht.model.internal.NominationId;
import de.twomartens.wahlrecht.model.internal.VotingResult;
import org.mapstruct.CollectionMappingStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(collectionMappingStrategy = CollectionMappingStrategy.ADDER_PREFERRED,
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface VotingResultMapper {

  default de.twomartens.wahlrecht.model.dto.v1.VotingResult mapToExternal(VotingResult result) {
    return new de.twomartens.wahlrecht.model.dto.v1.VotingResult(
        result.getNominationId().electionName(),
        result.getNominationId().partyAbbreviation(),
        result.getNominationId().name(),
        result.getVotesOnNomination(),
        result.getVotesThroughHealing(),
        result.getVotesPerPosition()
    );
  }

  default VotingResult mapToInternal(de.twomartens.wahlrecht.model.dto.v1.VotingResult result) {
    return new VotingResult(
        new NominationId(result.electionName(), result.partyAbbreviation(),
            result.nominationName()),
        result.votesOnNomination(),
        result.votesThroughHealing(),
        result.votesPerPosition()
    );
  }
}
