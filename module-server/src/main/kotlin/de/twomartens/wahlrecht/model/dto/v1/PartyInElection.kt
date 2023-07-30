package de.twomartens.wahlrecht.model.dto.v1

data class PartyInElection(var electionName: String?, var abbreviation: String?, var name: String?,
                           var nomination: Nomination?,
                           var constituencyNominations: Map<Int, Nomination>? = mapOf())
