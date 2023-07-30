package de.twomartens.wahlrecht.model.internal

data class ElectionResult(
    val electionName: String,
    val overallResults: Collection<VotingResult>,
    val constituencyResults: Map<Int, Collection<VotingResult>>
)