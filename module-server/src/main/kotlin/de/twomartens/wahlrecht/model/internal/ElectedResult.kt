package de.twomartens.wahlrecht.model.internal

data class ElectedResult(
    val electedCandidates: Map<VotingResult, Collection<ElectedCandidate>>,
    val usedElectionNumbers: List<Double>
) {
    val electedCandidatesByNomination = electedCandidates.entries.asSequence()
        .map { Pair(it.key.nominationId, it.value) }
        .toMap()
}
