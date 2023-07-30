package de.twomartens.wahlrecht.model.internal

data class SeatResult(val seatsPerResult: Map<VotingResult, Int>,
                      val usedElectionNumbers: List<Double>) {
    val seatsPerNomination = seatsPerResult.entries.asSequence()
        .map {Pair(it.key.nominationId, it.value)}
        .toMap()
}
