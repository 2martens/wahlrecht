package de.twomartens.wahlrecht.model.internal

data class VotingResult(
    val nominationId: NominationId, val votesOnNomination: Int,
    val votesThroughHealing: Int,
    val votesPerPosition: Map<Int, Int>
) {
    val totalVotes = votesOnNomination + votesThroughHealing + votesPerPosition.values
            .reduce{ acc: Int, value: Int -> acc + value }
    val totalVotesWithoutHealing = totalVotes - votesThroughHealing
}
