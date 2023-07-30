package de.twomartens.wahlrecht.model.internal

data class ElectedCandidate(val candidate: Candidate, val elected: Elected) {
    val name by candidate::name
    val profession by candidate::profession
}
