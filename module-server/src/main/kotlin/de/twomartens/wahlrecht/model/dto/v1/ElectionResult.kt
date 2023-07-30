package de.twomartens.wahlrecht.model.dto.v1

data class ElectionResult(val electionName: String, val overallResults: Collection<VotingResult>,
                          val constituencyResults: Map<Int, Collection<VotingResult>>)
