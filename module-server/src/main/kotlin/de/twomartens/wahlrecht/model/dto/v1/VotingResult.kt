package de.twomartens.wahlrecht.model.dto.v1

data class VotingResult(val electionName: String, val partyAbbreviation: String,
                        val nominationName: String,
                        val votesOnNomination: Int, val votesThroughHealing: Int,
                        val votesPerPosition: Map<Int, Int>)
