package de.twomartens.wahlrecht.model.dto.v1

data class ElectedCandidates(val overallResult: ElectedResult,
                             val constituencyResults: Map<Int, ElectedResult>,
                             val electionNumbersForSeatAllocation: List<Double>,
                             val seatAllocation: Map<String, Int>)
