package de.twomartens.wahlrecht.model.dto.v1

data class ElectedResult(val electedCandidates: Map<String, Collection<ElectedCandidate>>,
                         val usedElectionNumbers: List<Double>)
