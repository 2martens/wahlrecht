package de.twomartens.wahlrecht.model.dto.v1

import java.time.LocalDate

data class Election(val name: String, val day: LocalDate, val votingThreshold: VotingThreshold,
                    val totalNumberOfSeats: Int, val constituencies: Collection<Constituency>)
