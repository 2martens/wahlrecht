package de.twomartens.wahlrecht.model.db

data class VotingResult(
    var electionName: String,
    var partyAbbreviation: String,
    var nominationName: String,
    var votesOnNomination: Int = 0,
    var votesThroughHealing: Int = 0,
    var votesPerPosition: Map<Int, Int>
)
