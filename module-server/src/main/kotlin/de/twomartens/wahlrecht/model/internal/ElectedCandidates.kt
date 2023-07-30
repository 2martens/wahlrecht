package de.twomartens.wahlrecht.model.internal

data class ElectedCandidates(val overallResult: ElectedResult,
                             val constituencyResults: Map<Int, ElectedResult>,
                             val electionNumbersForSeatAllocation: List<Double>)
