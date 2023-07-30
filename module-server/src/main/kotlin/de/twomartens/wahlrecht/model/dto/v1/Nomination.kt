package de.twomartens.wahlrecht.model.dto.v1

data class Nomination(var electionName: String,
                      var partyAbbreviation: String,
                      var name: String,
                      var supportVotesOnNomination: Boolean,
                      var candidates: List<Candidate>)