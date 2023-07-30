package de.twomartens.wahlrecht.model.dto.v1

data class SeatResult(val seatsPerResult: Map<VotingResult, Int>,
                      val usedElectionNumbers: List<Double>)
